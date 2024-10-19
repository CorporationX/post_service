package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostCreateEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.post.PostKafkaEvent;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.producer.kafka.KafkaPostProducer;
import faang.school.postservice.publisher.PostCreatePublisher;
import faang.school.postservice.publisher.PostViewPublisher;
import faang.school.postservice.dto.event.PostViewEventDto;
import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.*;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundEntityException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import faang.school.postservice.repository.post.PostFilterRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final List<PostFilterRepository> postFilterRepository;
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final PostPublishService postPublishService;
    private final PostViewPublisher postViewPublisher;
    private final PostCreatePublisher postCreatePublisher;
    private final UserContext userContext;
    private final ModerationDictionary moderationDictionary;
    private final UserServiceClient userServiceClient;
    private final PostCacheRepository postCacheRepository;
    private final KafkaPostProducer kafkaPostProducer;
    private final UserCacheRepository userCacheRepository;
    private final UserMapper userMapper;

    @Value("${post.publisher.batch-size}")
    private int postsBatchSize;
    @Value("${post.moderator.count-posts-in-thread}")
    private int countPostsInThread;
    @Value("${spring.kafka.producer.followers-batch-size}")
    private int followersBatchSize;


    public void publishScheduledPosts() {
        log.info("Start publishing posts, at: {}", LocalDateTime.now());
        List<Post> posts = postRepository.findReadyToPublish();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < posts.size(); i += postsBatchSize) {
            int end = Math.min(i + postsBatchSize, posts.size());
            List<Post> batch = posts.subList(i, end);
            CompletableFuture<Void> future = postPublishService.publishBatch(batch)
                    .thenAccept(postRepository::saveAll);
            futures.add(future);
        }
        CompletableFuture<Void> allOfFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOfFutures.join();
        log.info("All posts successful published, at: {}", LocalDateTime.now());
    }

    @Transactional
    public PostDto create(@Valid @Nonnull PostCreateDto postCreateDto) {
        postValidator.checkIfPostHasAuthor(postCreateDto.getAuthorId(), postCreateDto.getProjectId());

        Post createdPost = postRepository.save(postMapper.toPost(postCreateDto));

        postCreatePublisher.publish(PostCreateEventDto.builder()
                .id(createdPost.getId())
                .userId(userContext.getUserId())
                .receivedAt(LocalDateTime.now())
                .build());

        return postMapper.toDto(createdPost);
    }

    @Transactional
    public PostDto publish(Long id) {
        Post post = getEntityById(id);
        postValidator.checkPostPublished(post.getId(), post.isPublished());
        post.setPublished(true);
        PostDto savedPostDto = postMapper.toDto(postRepository.save(post));
        savePostIntoCache(savedPostDto);
        sendEventToKafkaAndAuthorToCache(savedPostDto);
        return savedPostDto;
    }

    private void sendEventToKafkaAndAuthorToCache(PostDto savedPostDto) {
       userContext.setUserId(savedPostDto.getAuthorId());
       UserDto userDto =  userServiceClient.getUser(savedPostDto.getAuthorId());

       UserRedis userRedis =  userMapper.toRedis(userDto);
       userCacheRepository.save(userRedis);

       List<Long> followerIds = userDto.getFollowersId();
       List<List<Long>> followerIdBatches = ListUtils.partition(followerIds, followersBatchSize);
       for (var followerList : followerIdBatches) {
           PostKafkaEvent event = PostKafkaEvent.builder()
                   .postId(savedPostDto.getId())
                   .authorId(savedPostDto.getAuthorId())
                   .createdAt(savedPostDto.getCreatedAt())
                   .followerIds(followerList)
                   .build();
           kafkaPostProducer.sendEvent(event);
       }
    }

    private void saveAuthorToCache(UserDto userDto) {

    }

    private void savePostIntoCache(PostDto savedPostDto) {
        PostRedis postRedis = postMapper.toPostRedis(savedPostDto);
        postCacheRepository.save(postRedis);
    }

    @Transactional
    public PostDto update(@Valid @Nonnull PostUpdateDto postDto) {
        postValidator.validateForUpdating(postDto);

        Post postToUpdate = getEntityById(postDto.getId());
        postToUpdate.setContent(postDto.getContent());

        return postMapper.toDto(postRepository.save(postToUpdate));
    }

    @Transactional
    public void delete(Long id) {
        Post postToDelete = getEntityById(id);

        postRepository.save(postToDelete);
    }

    @Transactional
    public PostDto getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Post %s doesn't exist", id)));

        postViewPublisher.publish(PostViewEventDto.builder()
                .id(post.getId())
                .authorId(userContext.getUserId())
                .userId(id)
                .receivedAt(LocalDateTime.now())
                .build());

        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public Post validationAndPostReceived(LikeDto likeDto) {
        if (likeDto.getPostId() != null) {
            if (!postRepository.existsById(likeDto.getPostId())) {
                throw new DataValidationException("no such postId exists postId: " + likeDto.getPostId());
            }
        } else {
            throw new DataValidationException("arrived likeDto with postId equal to null");
        }
        return postRepository.findById(likeDto.getPostId()).orElseThrow(() ->
                new NotFoundEntityException("Not found post by id: " + likeDto.getPostId()));
    }

    @Transactional
    public Post getEntityById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Post %s doesn't exist", id)));
    }

    @Transactional
    public Page<PostDto> getPostsByPublishedStatus(PostFilterDto postFilter) {
        Optional<Specification<Post>> postSpecification = postFilterRepository.stream()
                .filter(filter -> filter.isApplicable(postFilter))
                .map(filter -> filter.apply(postFilter))
                .reduce(Specification::and);

        postSpecification.orElseThrow(() -> new DataValidationException("Required fields are incorrect"));

        Pageable pageRequest = postFilter.getPublished()
                ? PageRequest.of(postFilter.getPage(), postFilter.getSize(), Sort.by(SortField.PUBLISHED_AT.getValue()).descending())
                : PageRequest.of(postFilter.getPage(), postFilter.getSize(), Sort.by(SortField.CREATED_AT.getValue()).descending());

        return postRepository.findAll(postSpecification.get(), pageRequest).map(postMapper::toDto);
    }

    public void moderatePosts() {
        List<Post> posts = postRepository.findNotVerified();
        if (posts.isEmpty()) {
            return;
        }
        for (int i = 0; i < posts.size(); i += countPostsInThread) {
            if (i + countPostsInThread > posts.size()) {
                verifyPosts(posts.subList(i, posts.size()));
            } else
                verifyPosts(posts.subList(i, i + countPostsInThread));
        }
    }

    @Async
    public void verifyPosts(List<Post> posts) {
        Set<String> banWords = moderationDictionary.getBadWords();
        for (Post post : posts) {
            Optional<String> foundBanWord = banWords.stream()
                    .filter(x -> post.getContent().toLowerCase().contains(x))
                    .findFirst();

            if (foundBanWord.isPresent()) {
                log.info("Post with id {} contains banned word {}", post.getId(), foundBanWord.get());
                post.setVerified(false);
            } else {
                post.setVerified(true);
                post.setVerifiedDate(LocalDateTime.now());
            }
            postRepository.save(post);
        }
    }
}
