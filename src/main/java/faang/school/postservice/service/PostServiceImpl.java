package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Transactional
@Slf4j
public class PostServiceImpl implements PostService {

    private static final String POST_NOT_EXIST = "Post doesn't exist";

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final PostModerationDictionaryImpl moderationDictionary;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    @Value("${app.scheduling.post.max-posts-per-time}")
    private int limitToModerate;

    @Value("${post-service.post.count-of-unverified-posts-to-ban}")
    private int countOfUnverifiedPostsToBan;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final PostModerationDictionaryImpl moderationDictionary;

    @Value("${app.scheduling.post.max-posts-per-time}")
    private int limitToModerate;

    @Override
    public PostDto createDraft(PostDto postDto) {
        validatePostDto(postDto);
        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    public PostDto publishPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_EXIST));
        if (post.isPublished()) {
            throw new IllegalStateException("Post is already published");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    public PostDto updatePost(Long postId, PostDto postDto) {
        validatePostDto(postDto);
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_EXIST));
        if (postDto.getAuthorId() != null && !postDto.getAuthorId().equals(post.getAuthorId())) {
            throw new IllegalArgumentException("Cannot change the author of the post");
        }
        if (postDto.getProjectId() != null && !postDto.getProjectId().equals(post.getProjectId())) {
            throw new IllegalArgumentException("Cannot change the project of the post");
        }
        postMapper.update(postDto, post);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    public PostDto softDelete(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_EXIST));
        post.setDeleted(true);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    public PostDto getPostById(Long postId) {
        return postMapper.toDto(postRepository
                .findByIdWithLikes(postId).orElseThrow(() -> new NotFoundException("The post hasn't been found")));
    }

    @Override
    public List<PostDto> getAllDraftsByAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getAllDraftsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getAllPublishedPostsByAuthorId(Long authorId) {
        List<Post> posts = postRepository.findByAuthorIdWithLikes(authorId);
        return posts.stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getAllPublishedPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectIdWithLikes(projectId);
        return posts.stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public void moderatePosts() {
        List<Post> batch;
        int processedTotal = 0;
        while (!(batch = postRepository.findUnverifiedPosts(limitToModerate)).isEmpty()){
            List<CompletableFuture<Void>> futures = batch.stream()
                    .map(post -> CompletableFuture.runAsync(
                            () -> moderatePost(post),
                            taskScheduler.getScheduledExecutor()
                    ))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            processedTotal += batch.size();
            log.info("Processed batch of {} posts. Total processed: {}", batch.size(), processedTotal);
        }
    }

    public Post getPostEntryById(@Min(1) long id) {
        log.debug("Fetching post with ID: {}", id);

        log.debug("Search for fasting in the database");
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            log.error("Post with ID {} not found", id);
            throw new EntityNotFoundException("Post not found");
        }
        log.debug("Post with ID {} fetched successfully", id);
        return postOptional.get();
    }

    public void moderatePosts() {
        List<Post> batch;
        int processedTotal = 0;
        while (!(batch = postRepository.findUnverifiedPosts(limitToModerate)).isEmpty()){
            List<CompletableFuture<Void>> futures = batch.stream()
                    .map(post -> CompletableFuture.runAsync(
                            () -> moderatePost(post),
                            taskScheduler.getScheduledExecutor()
                    ))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            processedTotal += batch.size();
            log.info("Processed batch of {} posts. Total processed: {}", batch.size(), processedTotal);
        }
    }

    private void moderatePost(Post post) {
        boolean isClean = moderationDictionary.isTextWithoutForbiddenWords(post.getContent());
        post.setVerified(isClean);
        post.setVerifiedDate(LocalDateTime.now());
        postRepository.save(post);
        log.debug("Post {} moderated. Status: {}", post.getId(), isClean);
    }

    @Override
    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with id: " + id + " not found"));
    }

    @Override
    public void removeTagsFromPost(Long postId, List<Long> tagsId) {
        postRepository.deleteTagsFromPost(postId, tagsId);
    }

    @Override
    public void banUsersWithTooManyOffendedPosts() {
        List<Post> rejectedPosts = postRepository.findByVerifiedFalse();

        Map<Long, Long> authorRejectedCount = rejectedPosts.stream()
                .collect(Collectors.groupingBy(Post::getAuthorId, Collectors.counting()));

        List<Long> userIdsToBan = authorRejectedCount.entrySet().stream()
                .filter(user -> user.getValue() > countOfUnverifiedPostsToBan)
                .map(Map.Entry::getKey)
                .toList();
        log.info("Users ids {} sent to be baned", userIdsToBan);

        userIdsToBan.forEach(userId -> redisTemplate.convertAndSend(channelTopic.getTopic(), userId.toString()));
        log.info("Information to ban intruders has been sent via redis");
    }

    private void validatePostDto(PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new IllegalArgumentException("Post can have only one author: either user or project");
        }
        if (postDto.getAuthorId() != null) {
            UserDto userDto = userServiceClient.getUser(postDto.getAuthorId());
            if (userDto == null) {
                throw new NotFoundException("Author doesn't exist");
            }
        }
//        TODO: remove // when ProjectService has necessary Controller
//        if (postDto.getProjectId() != null) {
//            ProjectDto projectDto = projectServiceClient.getProject(postDto.getProjectId());
//            if (projectDto == null) {
//                throw new NotFoundException("Project doesn't exist");
//            }
//        }
    }
}
