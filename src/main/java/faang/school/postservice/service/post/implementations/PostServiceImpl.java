package faang.school.postservice.service.post.implementations;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.post.PostServiceConstants;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.batch.PostEventBatchSender;
import faang.school.postservice.service.post.interfaces.PostService;
import faang.school.postservice.service.post_correct.interfaces.PostCorrectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostCorrectService postCorrectService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostEventBatchSender postEventBatchSender;

    @Override
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with id " + postId + " not found"));
    }

    @Override
    @Transactional
    public PostDto createPostDraft(PostDto postDto) {
        validateDataForCreation(postDto);

        Post post = postRepository.save(postMapper.toEntity(postDto));

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    @CachePut(value = "posts", key = "#postId")
    public PostDto publishPost(PostDto postDto) {
        Post post = validateDataForPublication(postDto);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        post = postRepository.saveAndFlush(post);
        postEventBatchSender.sendBatch(post);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    @CachePut(value = "posts", key = "#postDto.id")
    public PostDto updatePost(PostDto postDto) {
        Post post = getPostIfExists(postDto.getId());

        post.setContent(postDto.getContent());

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostDto deletePost(PostDto postDto) {
        Post post = getPostIfExists(postDto.getId());

        post.setDeleted(true);

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    public PostDto getPost(PostDto postDto) {
        Post post = getPostIfExists(postDto.getId());

        return postMapper.toDto(post);
    }

    @Override
    public List<PostDto> getAuthorPostDrafts(PostDto postDto) {
        Long authorId = postDto.getAuthorId();
        return processPosts(postRepository.findByAuthorId(authorId),
                post -> !post.isPublished() && !post.isDeleted());
    }

    @Override
    public List<PostDto> getProjectPostDrafts(PostDto postDto) {
        Long projectId = postDto.getProjectId();
        return processPosts(postRepository.findByProjectId(projectId),
                post -> !post.isPublished() && !post.isDeleted());
    }

    @Override
    public List<PostDto> getAuthorPublishedPosts(PostDto postDto) {
        Long authorId = postDto.getAuthorId();
        return processPosts(postRepository.findByAuthorId(authorId),
                post -> post.isPublished() && !post.isDeleted());
    }

    @Override
    public List<PostDto> getProjectPublishedPosts(PostDto postDto) {
        Long projectId = postDto.getProjectId();
        return processPosts(postRepository.findByProjectId(projectId),
                post -> post.isPublished() && !post.isDeleted());
    }

    private List<PostDto> processPosts(List<Post> posts, Predicate<Post> filter) {
        return posts.stream()
                .filter(filter)
                .sorted(Comparator.comparing(Post::getCreatedAt, Comparator.reverseOrder()))
                .map(postMapper::toDto)
                .toList();
    }

    private Post getPostIfExists(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new PostDtoValidationException(String.format("Post with ID %d does not exist", id))
        );
    }

    private void validateDataForCreation(PostDto postDto) {
        if (postDto.getAuthorId() < 0 || postDto.getProjectId() < 0) {
            throw new PostDtoValidationException("ID should not be less than zero!");
        }
        if (postDto.getAuthorId() == 0 && postDto.getProjectId() == 0) {
            throw new PostDtoValidationException("One author required!");
        }
        if (postDto.getAuthorId() != 0 && postDto.getProjectId() != 0) {
            throw new PostDtoValidationException("The author can be either a user or a project!");
        }

        if (postDto.getAuthorId() != 0) {
            UserDto userDto = userServiceClient.getUser(postDto.getAuthorId());
            if (userDto.id() == 0) {
                throw new PostDtoValidationException(String.format(
                        "User with ID %d not found!", postDto.getAuthorId()));
            }
        } else {
            ProjectDto projectDto = projectServiceClient.getProject(postDto.getProjectId());
            if (projectDto.id() == 0) {
                throw new PostDtoValidationException(String.format(
                        "Project with ID %d not found!", postDto.getProjectId()));
            }
        }
    }

    private Post validateDataForPublication(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId()).orElseThrow(() ->
                new PostDtoValidationException(String.format("Post with ID %d does not exist", postDto.getId()))
        );

        if (post.isDeleted()) {
            throw new PostDtoValidationException(String.format(
                    "The post with ID %d removed", postDto.getId()));
        }

        if (post.isPublished()) {
            throw new PostDtoValidationException(String.format(
                    "The post with ID %d has already been published", postDto.getId()));
        }

        return post;
    }

    public void correctUnpublishedPosts() {
        List<Post> unpublishedPosts = postRepository.findReadyToPublish();
        log.info("Starting correction for {} unpublished posts", unpublishedPosts.size());
        if (unpublishedPosts.isEmpty()) {
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(
                PostServiceConstants.ThreadPool.EXECUTOR_POOL_THREAD_NUMBER);
        List<CompletableFuture<Void>> futures = unpublishedPosts.stream()
                .map(post -> postCorrectService.correctPost(post, executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .orTimeout(PostServiceConstants.TimeOut.CORRECT_POSTS_FUTURES_TIMEOUT, TimeUnit.SECONDS)
                .thenRun(() -> log.info("Finished correcting unpublished posts"))
                .exceptionally(throwable -> {
                    log.error("Correction unpublished posts process failed", throwable);
                    return null;
                })
                .join();

        shutdownExecutor(executor);
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(
                    PostServiceConstants.AwaitTermination.EXECUTOR_AWAIT_TERMINATION, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                log.warn("Executor did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("Executor shutdown interrupted", e);
        }
    }
}
