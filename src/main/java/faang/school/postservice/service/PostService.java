package faang.school.postservice.service;

import com.google.common.collect.Lists;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.CreatePostEvent;
import faang.school.postservice.dto.kafka.PostViewEvent;
import faang.school.postservice.exception.AlreadyDeletedException;
import faang.school.postservice.exception.AlreadyPostedException;
import faang.school.postservice.exception.NoPublishedPostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.publisher.KafkaPostProducer;
import faang.school.postservice.publisher.KafkaPostViewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.service.moderation.ModerationDictionary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserMapper redisUserMapper;
    private final ModerationDictionary moderationDictionary;
    private final Executor threadPoolForPostModeration;
    private final PublisherService publisherService;
    private final KafkaPostProducer kafkaPostProducer;
    private final KafkaPostViewProducer kafkaPostViewProducer;
    private final UserServiceClient userServiceClient;
    @Value("${post.moderation.scheduler.sublist-size}")
    private int sublistSize;

    public PostDto crateDraftPost(PostDto postDto) {

        postValidator.validateData(postDto);

        Post savedPost = postRepository.save(postMapper.toEntity(postDto));
        log.info("Draft post was created successfully, draftId={}", savedPost.getId());
        return postMapper.toDto(savedPost);
    }

    public PostDto publishPost(long postId) {
        Post post = postValidator.validatePostId(postId);

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
        savePostToRedis(post);
        UserDto userDto = userServiceClient.getUser(post.getAuthorId());
        saveUserToRedis(userDto);
        saveFeedToRedis(post.getId(), userDto);
        CreatePostEvent kafkaPostEvent = CreatePostEvent.builder()
                .postId(postId)
                .counterLikes(0L)
                .counterComments(0L)
                .build();
        kafkaPostProducer.publishPostEvent(kafkaPostEvent);
        log.info("Post was published successfully, postId={}", post.getId());
        return postMapper.toDto(post);
    }

    public PostDto updatePost(PostDto updatePost) {
        long postId = updatePost.getId();
        Post post = postValidator.validatePostId(postId);
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
        Post post = postValidator.validatePostId(postId);

        if (post.isDeleted()) {
            throw new AlreadyDeletedException("Post has been already deleted");
        }
        post.setDeleted(true);
        log.info("Post was soft-deleted successfully, postId={}", postId);
        return postMapper.toDto(post);
    }

    public PostDto getPost(long postId) {
        Post post = postValidator.validatePostId(postId);

        if (post.isDeleted()) {
            throw new AlreadyDeletedException("This post has been already deleted");
        }
        if (!post.isPublished()) {
            throw new NoPublishedPostException("This post hasn't been published yet");
        }
        publisherService.publishPostEventToRedis(post);
        saveViewToRedis(postId);
        RedisPost redisPost = redisPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        PostViewEvent kafkaPostViewEvent = PostViewEvent.builder()
                .postId(postId)
                .authorId(redisPost.getAuthorId())
                .build();
        kafkaPostViewProducer.publishPostViewEvent(kafkaPostViewEvent);
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

    private void checkListForObsceneWords(List<Post> list) {
        list.forEach(post -> {
            boolean checkResult = moderationDictionary.checkWordContent(post.getContent());
            log.info("Post, id={} has been checked for content obscene words", post.getId());
            post.setVerified(!checkResult);
            post.setVerifiedDate(LocalDateTime.now());
        });
        postRepository.saveAll(list);
    }

    public void saveViewToRedis(long postId) {
        RedisPost redisPost = redisPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found in Redis by id: {}" + postId));
        redisPost.setPostViews(redisPost.getPostViews() + 1);
        redisPostRepository.save(redisPost);
    }

    public void savePostToRedis(Post post) {
        RedisPost redisPost = redisPostMapper.toRedisPost(post);
        redisPost.setPostViews(0L);
        redisPost.setVersion(1L);
        redisPostRepository.save(redisPost);
    }

    public void saveUserToRedis(UserDto userDto) {
        RedisUser redisUser = redisUserMapper.toRedisUser(userDto);
        redisUser.setVersion(1L);
        redisUserRepository.save(redisUser);
    }

    public void saveFeedToRedis(long postId, UserDto userDto) {
        List<Long> listFollowers = userDto.getFollowers();
        for (Long followerId : listFollowers) {
            LinkedHashSet<Long> postIds = postRepository.findByAuthorId(userDto.getId()).stream()
                    .map(Post::getId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            postIds.add(postId);
            RedisFeed redisFeed = RedisFeed.builder()
                    .userId(followerId)
                    .postIds(postIds)
                    .version(1L)
                    .build();
            redisFeedRepository.save(redisFeed);
        }
    }

    public List<PostDto> getPostsFromBeginningInDb(List<Long> followees, int sizeOfPosts) {
        return postRepository.getFirstsPostsBySubscribers(followees, sizeOfPosts).stream()
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> getPostsAfterPostInDb(List<Long> followees, int sizeOfPosts, Long point) {
        return postRepository.getPostsBySubscribersFromPoint(followees, sizeOfPosts, point).stream()
                .map(postMapper::toDto)
                .toList();
    }
}