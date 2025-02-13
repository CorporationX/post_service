package faang.school.postservice.service;

import faang.school.postservice.dto.AuthorPostCount;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.user.UserCacheDto;
import faang.school.postservice.event.PostEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserCacheRepository;
import faang.school.postservice.utils.PostSpecifications;
import faang.school.postservice.validator.HashtagValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final HashtagService hashtagService;
    private final HashtagValidator hashtagValidator;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ExecutorService executorService;
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final KafkaPostProducer kafkaPostProducer;
    private final UserFeignService userFeignService;

    @Value("${spring.data.redis.channel.user-bans-channel}")
    private String userBansChannelName;
    private final PostVerificationService postVerificationService;

    @Value("${ad.batch.size}")
    private int batchSize;


    @Transactional
    public ResponsePostDto create(CreatePostDto createPostDto) {
        postValidator.validateContent(createPostDto.getContent());
        postValidator.validateAuthorIdAndProjectId(createPostDto.getAuthorId(), createPostDto.getProjectId());
        postValidator.validateAuthorId(createPostDto.getAuthorId());
        postValidator.validateProjectId(createPostDto.getProjectId(), createPostDto.getAuthorId());

        validateHashtags(createPostDto.getHashtags());

        if (createPostDto.getAuthorId() != null && createPostDto.getProjectId() != null) {
            createPostDto.setProjectId(null);
        }

        Post entity = postMapper.toEntity(createPostDto);

        entity.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC+3")));
        entity.setPublished(false);
        entity.setDeleted(false);

        if (hasHashtags(createPostDto.getHashtags())) {
            entity.setHashtags(getAndCreateHashtags(createPostDto.getHashtags()));
        }

        Post savedPost = postRepository.save(entity);

        sendPostEvent(savedPost);
        postCacheRepository.savePostToCache(postMapper.toRedisPostDto(savedPost));

        return postMapper.toDto(entity);
    }

    @Transactional
    public ResponsePostDto publish(Long postId) {
        postValidator.validateExistingPostId(postId);
        postValidator.validatePostIdOnPublished(postId);

        Post post = postRepository.findById(postId).get();

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now(ZoneId.of("UTC+3")));
        post.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC+3")));

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Transactional
    public ResponsePostDto update(Long postId, UpdatePostDto updatePostDto) {
        postValidator.validateExistingPostId(postId);
        postValidator.validateContent(updatePostDto.getContent());

        validateHashtags(updatePostDto.getHashtags());

        Post post = postRepository.findById(postId).get();


        if (hasHashtags(updatePostDto.getHashtags())) {
            post.setHashtags(getAndCreateHashtags(updatePostDto.getHashtags()));
        }

        post.setContent(updatePostDto.getContent());
        post.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC+3")));

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Transactional
    public void delete(Long postId) {
        postValidator.validateExistingPostId(postId);
        postValidator.validatePostIdOnRemoved(postId);

        Post post = postRepository.findById(postId).get();

        post.setDeleted(true);

        postRepository.save(post);
    }

    public ResponsePostDto getById(Long postId) {
        postValidator.validateExistingPostId(postId);
        postValidator.validatePostIdOnRemoved(postId);

        return postMapper.toDto(postRepository.findById(postId).get());
    }

    public List<ResponsePostDto> getDraftsByUserId(Long userId) {
        postValidator.validateAuthorId(userId);

        return postRepository.findReadyToPublishByAuthor(userId).stream().map(postMapper::toDto).toList();
    }

    public List<ResponsePostDto> getDraftsByProjectId(Long userId) {
        postValidator.validateAuthorId(userId);

        return postRepository.findReadyToPublishByProject(userId).stream().map(postMapper::toDto).toList();
    }

    public List<ResponsePostDto> getPublishedByUserId(Long userId) {
        postValidator.validateAuthorId(userId);

        return postRepository.findPublishedByAuthor(userId).stream().map(postMapper::toDto).toList();
    }

    public List<ResponsePostDto> getPublishedByProjectId(Long projectId, Long authorId) {
        postValidator.validateProjectId(projectId, authorId);

        return postRepository.findPublishedByProject(projectId).stream().map(postMapper::toDto).toList();
    }

    public List<ResponsePostDto> findByHashtags(String tag) {
        hashtagValidator.validateHashtag(tag);

        return postRepository.findByHashtags(tag).stream().map(postMapper::toDto).toList();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post with id: %s not found", id)));
    }

    private void validateHashtags(List<String> hashtags) {
        if (hashtags != null) {
            for (String hashtag : hashtags) {
                System.out.println(hashtag);
                hashtagValidator.validateHashtag(hashtag);
            }
        }
    }

    private boolean hasHashtags(List<String> hashtags) {
        return hashtags != null && !hashtags.isEmpty();
    }

    private Set<Hashtag> getAndCreateHashtags(List<String> hashtags) {
        Map<String, Hashtag> existingHashtags = hashtagService.findAllByTags(hashtags)
                .stream()
                .collect(Collectors.toMap(Hashtag::getTag, Function.identity()));

        Set<Hashtag> result = new HashSet<>();

        for (String tag : hashtags) {
            Hashtag hashtag = existingHashtags.computeIfAbsent(tag, hashtagService::create);
            result.add(hashtag);
        }
        return result;
    }

    public void banOffensiveAuthors() {
        List<AuthorPostCount> unverifiedPostsByAuthor = getUnverifiedPostsGroupedByAuthor();

        List<AuthorPostCount> offensiveAuthors = unverifiedPostsByAuthor.stream()
                .filter(entry -> entry.getPostCount() > 5)
                .toList();

        log.info("Found {} authors with more than 5 unverified posts", offensiveAuthors.size());

        offensiveAuthors.forEach(entry -> {
            Long authorId = entry.getAuthorId();
            redisTemplate.convertAndSend(userBansChannelName, authorId);
        });
    }

    private List<AuthorPostCount> getUnverifiedPostsGroupedByAuthor() {
        List<Object[]> rawResults = postRepository.findUnverifiedPostsGroupedByAuthor();

        return rawResults.stream()
                .map(result -> new AuthorPostCount((Long) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }


    @Transactional
    public void checkAndVerifyPosts() {
        List<Post> postsToVerify = postRepository.findAllByVerifiedDateIsNull();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < postsToVerify.size(); i += batchSize) {
            int end = Math.min(i + batchSize, postsToVerify.size());
            List<Post> batch = postsToVerify.subList(i, end);

            CompletableFuture<Void> future = postVerificationService.checkAndVerifyPostsInBatch(batch);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void publishScheduledPosts() {
        List<Post> postsToPublish = postRepository.findAll(PostSpecifications.isReadyToPublish());

        if (postsToPublish.isEmpty()) {
            log.info("No posts to publish at this time");
            return;
        }

        log.info("Starting batch processing for {} posts", postsToPublish.size());

        for (int i = 0; i < postsToPublish.size(); i += batchSize) {
            int end = Math.min(i + batchSize, postsToPublish.size());
            List<Post> batch = postsToPublish.subList(i, end);

            CompletableFuture.runAsync(() -> {
                log.info("Processing batch of size {}", batch.size());
                try {
                    LocalDateTime now = LocalDateTime.now();
                    for (Post post : batch) {
                        post.setPublished(true);
                        post.setPublishedAt(now);
                    }
                    postRepository.saveAll(batch);
                    log.info("Successfully saved batch of size {}", batch.size());
                } catch (Exception e) {
                    log.error("Error while processing batch: {}", batch, e);
                }
            }, executorService);
        }

        log.info("Batch processing completed");
    }

    public List<Post> getReadyToPublishPosts() {
        return postRepository.findAll(PostSpecifications.isReadyToPublish());
    }

    private UserCacheDto findPostsAuthor(Long authorId) {
        UserCacheDto cachedUser = userCacheRepository.getUserFromCache(authorId.toString());
        if (cachedUser != null) {
            return cachedUser;
        } else {
            UserCacheDto userFromDb = userFeignService.getCacheUser(authorId);
            if (userFromDb != null) {
                userCacheRepository.saveUserToCache(userFromDb);
            }
            return userFromDb;
        }
    }

    private void sendPostEvent(Post post) {
        UserCacheDto author = findPostsAuthor(post.getAuthorId());
        PostEvent postEvent = PostEvent.create(post.getId(), post.getAuthorId(), author.followeesIds());
        kafkaPostProducer.sendPostEvent(postEvent);
    }
}
