package faang.school.postservice.service;

import com.google.common.collect.Lists;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.PostPair;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import faang.school.postservice.dto.kafka.KafkaPostViewEvent;
import faang.school.postservice.exception.AlreadyDeletedException;
import faang.school.postservice.exception.AlreadyPostedException;
import faang.school.postservice.exception.NoPublishedPostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.publisher.KafkaPostProducer;
import faang.school.postservice.publisher.KafkaPostViewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PublisherService publisherService;
    private final RedisCacheService redisCacheService;
    private final PostRepository postRepository;
    private final RedisPostRepository redisPostRepository;
    private final PostMapper postMapper;
    private final RedisPostMapper redisPostMapper;
    private final ModerationDictionary moderationDictionary;
    private final KafkaPostProducer kafkaPostPublishEventPublisher;
    private final KafkaPostViewProducer kafkaPostViewEventPublisher;
    private final Executor threadPoolForPostModeration;
    private final ThreadPoolTaskExecutor postEventTaskExecutor;
    private final PostValidator postValidator;
    @Value("${post.moderation.scheduler.sublist-size}")
    private int sublistSize;
    @Value("${spring.data.kafka.util.batch-size}")
    private int batchSize;

    @Transactional
    public PostDto crateDraftPost(PostDto postDto) {
        postValidator.validateData(postDto);

        Post savedPost = postRepository.save(postMapper.toEntity(postDto));
        log.info("Draft post was created successfully, draftId={}", savedPost.getId());
        return postMapper.toDto(savedPost);
    }

    @Transactional
    public PostDto publishPost(long postId) {
        Post post = findPostBy(postId);

        if (post.isPublished() || (post.getScheduledAt() != null
                && post.getScheduledAt().isAfter(LocalDateTime.now()))) {
            throw new AlreadyPostedException("You cannot published post, that had been already published");
        }
        if (post.isDeleted()) {
            throw new AlreadyDeletedException(("You cannot publish post, that had been deleted"));
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        publisherService.publishPostEventToRedis(post);
        log.info("Post was published successfully, postId={}", post.getId());

        savePostAndAuthorToRedisAndSendEventToKafka(post);
        return postMapper.toDto(post);
    }

    public PostDto updatePost(PostDto updatePost) {
        long postId = updatePost.getId();
        Post post = findPostBy(postId);
        postValidator.validateAuthorUpdate(post, updatePost);
        LocalDateTime updateScheduleAt = updatePost.getScheduledAt();

        if (updateScheduleAt != null && updateScheduleAt.isAfter(post.getScheduledAt())) {
            post.setScheduledAt(updateScheduleAt);
        }

        post.setContent(updatePost.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        log.info("Post was updated successfully, postId={}", post.getId());
        return postMapper.toDto(post);
    }

    public PostDto softDelete(long postId) {
        Post post = findPostBy(postId);

        if (post.isDeleted()) {
            throw new AlreadyDeletedException("Post has been already deleted");
        }
        post.setDeleted(true);
        log.info("Post was soft-deleted successfully, postId={}", postId);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto getPost(long postId) {
        Post post = findPostBy(postId);

        if (post.isDeleted()) {
            throw new AlreadyDeletedException("This post has been already deleted");
        }
        if (!post.isPublished()) {
            throw new NoPublishedPostException("This post hasn't been published yet");
        }
        publisherService.publishPostEventToRedis(post);
        publishPostViewEventToKafka(postId);

        log.info("Post has taken from DB successfully, postId={}", postId);
        return postMapper.toDto(post);
    }

    public List<PostDto> getUserDrafts(long userId) {
        postValidator.validateUserId(userId);

        List<PostDto> userDrafts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        log.info("User's drafts have taken from DB successfully, userId={}", userId);
        return userDrafts;
    }

    public List<PostDto> getProjectDrafts(long projectId) {
        postValidator.validateProjectId(projectId);

        List<PostDto> projectDrafts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        log.info("Drafts of project have taken from DB successfully, projectId={}", projectId);
        return projectDrafts;
    }

    public List<PostDto> getUserPosts(long userId) {
        postValidator.validateUserId(userId);

        List<PostDto> userPosts = postRepository.findByAuthorIdWithLikes(userId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(post -> {
                    publisherService.publishPostEventToRedis(post);
                    return postMapper.toDto(post);
                })
                .toList();


        log.info("User's posts have taken from DB successfully, userId={}", userId);
        return userPosts;
    }

    public List<PostDto> getProjectPosts(long projectId) {
        postValidator.validateProjectId(projectId);

        List<PostDto> projectPosts = postRepository.findByProjectIdWithLikes(projectId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(post -> {
                    publisherService.publishPostEventToRedis(post);
                    return postMapper.toDto(post);
                })
                .toList();

        log.info("Posts of project have taken from DB successfully, projectId={}", projectId);
        return projectPosts;
    }

    public void doPostModeration() {
        log.info("<doPostModeration> was called successfully");
        List<Post> notVerifiedPost = postRepository.findNotVerified();
        List<List<Post>> partitionList = new ArrayList<>();

        if (notVerifiedPost.size() > sublistSize) {
            partitionList = Lists.partition(notVerifiedPost, sublistSize);
        } else {
            partitionList.add(notVerifiedPost);
        }

        partitionList.forEach(list -> threadPoolForPostModeration.execute(() -> checkListForObsceneWords(list)));
        log.info("All posts have checked successfully");
    }

    @Transactional
    public Post findPostBy(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with ID: %d, doesn't exist", postId)));
    }

    @Async("postViewsTaskExecutor")
    public void incrementPostView(long postId) {
        Optional<RedisPost> redisPost = redisCacheService.findByRedisPostBy(postId);

        postRepository.incrementPostViewByPostId(postId);
        log.info("Post with ID: {}, has been successfully incremented his view", postId);

        if (redisPost.isPresent()) {
            RedisPost post = redisPost.get();

            log.info("Post with ID: {} exist in Redis. Amount of Post views {}", postId, post.getPostViews());
            post.incrementPostView();
            post.incrementPostVersion();

            redisCacheService.updateRedisPost(postId, post);
        } else {
            log.warn("Post with ID {} are not exist in Redis. Attempting to retrieve it from the database.", postId);
            RedisPost post = mapPostToRedisPost(findPostBy(postId));

            redisCacheService.saveRedisPost(post);
        }
    }

    @Transactional
    public Post findAlredyPublishedAndNotDeletedPost(long postId) {
        return postRepository.findPublishedAndNotDeletedBy(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with ID: %d, are not published yet or already deleted", postId)));
    }

    @Transactional
    public List<Post> findSortedPostsByAuthorIdsLimit(List<Long> authorIds, long requiredAmount) {
        return postRepository.findSortedPostsByAuthorIdsAndLimit(authorIds, requiredAmount);
    }

    @Transactional
    public List<Post> findSortedPostsByAuthorIdsNotInPostIdsLimit(List<Long> userIds, List<Long> usedPostIds, int amount) {
        return postRepository.findSortedPostsByAuthorIdsNotInPostIdsLimit(userIds, usedPostIds, amount);
    }

    @Transactional
    public List<Post> findSortedPostsFromPostDateAndAuthorsLimit(List<Long> followees, LocalDateTime lastPostDate, int limit) {
        return postRepository.findSortedPostsFromPostDateAndAuthorsLimit(followees, lastPostDate, limit);
    }

    public List<RedisPost> findRedisPostsByAndCacheThemIfNotExist(List<Long> postIds) {
        return postIds.stream()
                .map(this::findRedisPostAndCacheHimIfNotExist)
                .collect(Collectors.toList());
    }

    @Transactional
    public RedisPost findRedisPostAndCacheHimIfNotExist(long postId) {
        return redisPostRepository.findById(postId)
                .orElseGet(() -> mapPostToRedisPost(findPostBy(postId)));
    }

    @Transactional
    public RedisPost findRedisPostBy(long postId) {
        return redisPostRepository.findById(postId)
                .orElseGet(() -> mapPostToRedisPost(findPostBy(postId)));
    }

    private RedisPost findPostAndCacheHim(long postId) {
        log.warn("Post with ID: {} was not found in Redis. Attempting to retrieve from the database and cache in Redis.", postId);

        RedisPost redisPost = mapPostToRedisPost(findAlredyPublishedAndNotDeletedPost(postId));
        return redisCacheService.saveRedisPost(redisPost);
    }

    private void checkListForObsceneWords(List<Post> list) {
        list.forEach(post -> {
            boolean checkResult = moderationDictionary.checkWordContent(post.getContent());
            log.info("Post, id={} has been checked for content obscene words", post.getId());
            post.setVerified(!checkResult);
            post.setVerifiedDate(LocalDateTime.now());
        });
        postRepository.saveAll(list);
    }

    private void savePostAndAuthorToRedisAndSendEventToKafka(Post post) {
        long postId = post.getId();
        long authorId = post.getAuthorId();

        UserDto authorDto = redisCacheService.findUserBy(authorId);

        redisCacheService.saveRedisPost(mapPostToRedisPost(post));
        log.info("Post with ID: {}, were successfully save to Redis", postId);

        RedisUser redisUser = redisCacheService.mapUserToRedisUser(authorDto);
        redisCacheService.saveRedisUser(redisUser);
        log.info("User with ID: {}, were successfully save or updated in Redis", authorId);

        PostPair postPair = buildPostPair(postId, post.getPublishedAt());
        publishPostEventToKafka(authorDto.getFollowerIds(), postPair);
    }

    private PostPair buildPostPair(long postId, LocalDateTime publishedAt) {
        return PostPair.builder()
                .postId(postId)
                .publishedAt(publishedAt)
                .build();
    }

    public RedisPost mapPostToRedisPost(Post post) {
        RedisPost redisPost = redisPostMapper.toRedisPost(post);
        redisPost.setVersion(1);

        return redisPost;
    }

    private void publishPostEventToKafka(List<Long> followersIds, PostPair postPair) {
        postEventTaskExecutor.execute(() -> {
            for (int i = 0; i < followersIds.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, followersIds.size());
                List<Long> sublist = followersIds.subList(i, endIndex);

                KafkaPostEvent event = KafkaPostEvent.builder()
                        .postPair(postPair)
                        .followersIds(sublist)
                        .build();

                kafkaPostPublishEventPublisher.publish(event);
            }
        });
    }

    public void publishPostViewEventToKafka(long postId) {
        KafkaPostViewEvent event = KafkaPostViewEvent.builder()
                .postId(postId)
                .build();

        kafkaPostViewEventPublisher.publish(event);
    }
}