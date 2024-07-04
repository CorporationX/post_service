package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostKafkaEvent;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerifyStatus;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import faang.school.postservice.repository.UserJdbcRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final PostViewEventPublisher postViewEventPublisher;
    private final ModerationDictionary moderationDictionary;
    private final HashtagService hashtagService;
    private final RedisPostRepository redisPostRepository;
    private final KafkaPostProducer kafkaPostProducer;
    private final UserJdbcRepository userJdbcRepository;
    private final UserServiceClient userServiceClient;
    private final RedisUserRepository redisUserRepository;
    private final UserContext userContext;
    private final LikeMapper likeMapper;

    @Transactional
    public PostDto create(PostDto postDto) {
        log.info("Trying to create post with ID: {}", postDto.getId());
        postValidator.validateAuthorIdAndProjectId(postDto.getAuthorId(), postDto.getProjectId());
        Post post = postRepository.saveAndFlush(postMapper.toEntity(postDto));
        hashtagService.parsePostAndCreateHashtags(post);
        log.info("Post with ID:{} created.", postDto.getId());
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publish(Long postId) {
        log.info("Trying to publish post with ID: {}", postId);
        Post post = findById(postId);
        postValidator.validatePublicationPost(post);
        post.setPublished(true);
        log.info("Post with ID: {} published.", postId);
        PostDto postDto = postMapper.toDto(post);
        addToRedisAndSendEvents(postMapper.toRedis(post));
        return postDto;
    }

    private void addToRedisAndSendEvents(PostRedis postRedis) {
        Long authorId = postRedis.getAuthorId();
        userContext.setUserId(authorId);
        UserDto userDto = userServiceClient.getUser(authorId);
        log.info("Save user with ID: {} to Redis", authorId);
        redisUserRepository.save(new UserRedis(userDto.getId(), userDto.getUsername()));
        log.info("Save post with ID: {} to Redis", postRedis.getId());
        redisPostRepository.save(postRedis);
        PostKafkaEvent postKafkaEvent = new PostKafkaEvent(authorId, userJdbcRepository.getSubscribers(authorId));
        log.info("Send event with Post ID: {} to Kafka", postRedis.getId());
        kafkaPostProducer.sendEvent(postKafkaEvent);
    }

    @Transactional
    public PostDto update(Long postId, String content, LocalDateTime publicationTime) {
        log.info("Trying to update post with ID: {}", postId);
        Post post = findById(postId);
        post.setContent(content);
        post.setPublishedAt(publicationTime);
        log.info("Post with ID:{} created. Content updated on {}", postId, content);
        return postMapper.toDto(post);
    }

    @Transactional
    public void deleteById(Long postId) {
        log.info("Trying to delete post with ID: {}", postId);
        Post post = findById(postId);
        post.setDeleted(true);
        log.info("A post with this ID: {} has been added to the deleted list.", postId);
    }

    @Transactional
    public PostDto getPostById(long userId, Long postId) {
        log.info("Trying to get a post by ID: {}", postId);
        Post post = findById(postId);
        PostDto postDto = postMapper.toDto(post);
        log.info("Post with ID {} received successfully", postId);
        publishPostViewEvent(userId, post);
        return postDto;
    }

    @Transactional
    public List<PostDto> getAllPostsDraftsByUserAuthorId(Long authorId) {
        log.info("Trying to get drafts of posts, where the author is a user with ID: {}", authorId);
        List<Post> posts = postRepository.findByAuthorId(authorId);
        List<PostDto> draftsPostsByUser = getNonDeletedPosts(posts, (post -> !post.isPublished()));
        log.info("Found {} posts for author with ID: {}", draftsPostsByUser.size(), authorId);
        return draftsPostsByUser;
    }

    @Transactional
    public List<PostDto> getAllPostsDraftsByProjectAuthorId(Long projectId) {
        log.info("Trying to get drafts of posts, where the author is a project with ID: {}", projectId);
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<PostDto> draftsPostsByProject = getNonDeletedPosts(posts, (post -> !post.isPublished()));
        log.info("Found {} posts for author with ID: {}", draftsPostsByProject.size(), projectId);
        return draftsPostsByProject;
    }

    @Transactional
    public List<PostDto> getAllPublishedNonDeletedPostsByUserAuthorId(long userId, Long authorId) {
        log.info("Trying to get all published, non-deleted posts authored by a user with a given id: {}", authorId);
        List<Post> posts = postRepository.findByAuthorId(authorId);
        List<PostDto> publishedPostsByUser = getNonDeletedPosts(posts, (Post::isPublished));
        log.info("Found {} posts for author with ID: {}", publishedPostsByUser.size(), authorId);
        viewEvents(userId, publishedPostsByUser);
        return publishedPostsByUser;
    }

    @Transactional
    public List<PostDto> getAllPublishedNonDeletedPostsByProjectAuthorId(long userId, Long projectId) {
        log.info("Trying to get all published, non-deleted posts authored by a project with a given id: {}", projectId);
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<PostDto> publishedPostsByProject = getNonDeletedPosts(posts, (Post::isPublished));
        log.info("Found {} posts for author with ID: {}", publishedPostsByProject.size(), projectId);
        viewEvents(userId, publishedPostsByProject);
        return publishedPostsByProject;
    }

    public Post findById(Long postId) {
        log.info("Attempting to find post with ID: {}", postId);
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error(String.format("Post with this ID: %s was not found", postId));
                    return new DataValidationException(String.format("Post with this ID: %s was not found", postId));
                });
    }

    private List<PostDto> getNonDeletedPosts(List<Post> posts, Predicate<Post> predicate) {
        return posts.stream()
                .filter(predicate)
                .filter(post -> !post.isDeleted())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }

    private void publishPostViewEvent(long userId, Post post) {
        log.info("Trying to publish post {} view event...", post.getId());
        PostViewEvent event = PostViewEvent.builder()
                .postId(post.getId())
                .userId(userId)
                .build();

        if (post.getAuthorId() != null) {
            event.setAuthorId(post.getAuthorId());
        }
        if (post.getProjectId() != null) {
            event.setAuthorId(post.getProjectId());
        }

        postViewEventPublisher.publish(event);
        log.info("Post {} view event published.", post.getId());
    }

    private void viewEvents(long userId, List<PostDto> postsDtos) {
        postsDtos.stream()
                .map(postMapper::toEntity)
                .forEach(post -> publishPostViewEvent(userId, post));
    }

    @Transactional
    public void moderateAll() {
        log.info("Moderate posts");
        List<Post> posts = postRepository.findNotVerifiedPosts();
        posts.forEach(post -> {
            VerifyStatus status = moderationDictionary.checkString(post.getContent()) ? VerifyStatus.VERIFIED : VerifyStatus.NOT_VERIFIED;
            post.setVerifyStatus(status);
            post.setVerifiedDate(LocalDateTime.now());
        });
    }

    @Cacheable(value = "posts", key = "#hashtag")
    public List<PostDto> findByHashtag(String hashtag) {
        log.info("Find posts with hashtag: {}", hashtag);
        List<Post> posts = hashtagService.findByName(hashtag).getPosts();
        return postMapper.toListDto(posts);
    }
}