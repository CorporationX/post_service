package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.ProjectDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import faang.school.postservice.publisher.kafka_producer.KafkaPostProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.validator.PostValidator;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostValidator postValidator;
    private final PostRepository postRepository;
    private final AsyncPostPublishService asyncPostPublishService;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;
    private final ResourceService resourceService;
    private final ModeratePostService moderatePostService;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final KafkaPostProducer kafkaPostProducer;

    @Value("${scheduler.post-publisher.size_batch}")
    private int sizeSublist;

    @Value("${scheduler.moderation.post.batch_size}")
    int postBatchSize;

    @Transactional
    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public PostDto createDraftPost(PostDto postDto, @Nullable MultipartFile file) {
        UserDto author = null;
        ProjectDto project = null;

        if (postDto.getAuthorId() != null) {
            author = userServiceClient.getUser(postDto.getAuthorId());
        } else if (postDto.getProjectId() != null) {
            project = projectServiceClient.getProject(postDto.getProjectId());
        }
        postValidator.validateAuthorExists(author, project);
        Post savePost = savePost(postDto);
        if (file != null) {
            resourceService.addResource(savePost, file);
        }
        return postMapper.toDto(savePost);
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public PostDto publishPost(long id) {
        Post post = findById(id);
        postValidator.validateIsNotPublished(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        savePostToRedis(post);
        saveAuthorToRedis(post.getAuthorId());
        kafkaPostProducer.publishPostKafkaEvent(post);
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto updatePost(UpdatePostDto postDto, long postId, @Nullable MultipartFile file) {
        Post post = findById(postId);
        post.setContent(postDto.getContent());
        if (file != null) {
            resourceService.addResource(post, file);
        } else if (postDto.getResourceId() != null) {
            resourceService.deleteResource(post, postDto.getResourceId());
        }
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public void deletePost(long id) {
        Post post = findById(id);
        if (post.isDeleted()) {
            throw new DataValidationException("Пост уже удален");
        } else {
            post.setDeleted(true);
            postRepository.save(post);
        }
    }

    @Transactional(readOnly = true)
    public PostDto getPost(long id) {
        Post post = findById(id);
        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getDraftsByUser(long userId) {
        List<Post> foundedPosts = postRepository.findByAuthorId(userId);
        return getSortedDrafts(foundedPosts);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getDraftsByProject(long projectId) {
        List<Post> foundedPosts = postRepository.findByProjectId(projectId);
        return getSortedDrafts(foundedPosts);
    }

    @Transactional
    public List<PostDto> getPublishedPostsByUser(long userId) {
        List<Post> foundedPosts = postRepository.findByAuthorIdWithLikes(userId);
        return getSortedPublished(foundedPosts);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPublishedPostsByProject(long projectId) {
        List<Post> foundedPosts = postRepository.findByProjectIdWithLikes(projectId);
        return getSortedPublished(foundedPosts);
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new faang.school.postservice.exception.DataValidationException("Post has not found"));
    }

    @Transactional
    public void publishScheduledPosts() {
        log.info("Started publish posts from scheduler");
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Post> postsToPublish = postRepository.findReadyToPublish();
        if (!postsToPublish.isEmpty()) {
            log.info("Size of posts list publish is {}", postsToPublish.size());
            List<List<Post>> subLists = ListUtils.partition(postsToPublish, sizeSublist);
            subLists.forEach(asyncPostPublishService::publishPost);
            log.info("Finished publish all posts at {}", currentDateTime);
        } else {
            log.info("Unpublished posts at {} not found", currentDateTime);
        }
    }

    public void moderatePosts() {
        List<Post> posts = postRepository.findAllByVerifiedDateIsNull();
        List<List<Post>> postBathes = ListUtils.partition(posts, postBatchSize);
        postBathes.forEach(moderatePostService::moderatePostBatch);
    }

    private Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с указанным ID не существует"));
    }

    private List<PostDto> getSortedDrafts(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();
    }

    private Post savePost(PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        post.setVerified(false);
        return postRepository.save(post);
    }

    private List<PostDto> getSortedPublished(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted((post1, post2) -> post2.getPublishedAt().compareTo(post1.getPublishedAt()))
                .map(postMapper::toDto)
                .toList();
    }

    private void savePostToRedis(Post post) {
        PostCache postCache = new PostCache(post);
        redisPostRepository.save(postCache);
        log.info("Post {} saved to redis", post.getId());
    }

    private void saveAuthorToRedis(long userId) {
        UserDto author = userServiceClient.getUser(userId);
        UserCache userCache = new UserCache(author);
        redisUserRepository.save(userCache);
        log.info("User {} saved to redis", author.getUsername());
    }
}