package faang.school.postservice.service.post.implementations;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.component.post.PostEventBatchSender;
import faang.school.postservice.config.post.PostServiceConstants;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.PostScheduledProcessingException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.interfaces.PostService;
import faang.school.postservice.service.post_check.interfaces.PostCheckerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostCheckerService postCorrectService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ExecutorService postPublishPool;
    private final PlatformTransactionManager transactionManager;
    private final PostEventBatchSender postEventBatchSender;

    public static final int POST_PUBLISH_POOL_SIZE = 10;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Override
    public CompletableFuture<Void> publishScheduledPosts() {
        if (!canStartPublishing()) {
            log.warn("Previous task still running, skipping...");
            return CompletableFuture.completedFuture(null);
        }

        try {
            List<Post> posts = fetchPostsToPublish();
            if (posts.isEmpty()) {
                log.warn("Post list is empty");
                return CompletableFuture.completedFuture(null);
            }

            List<List<Post>> chunks = prepareChunks(posts);
            return processPublishPostChunks(chunks);
        } catch (Exception e) {
            log.error("Error while starting chunk processing", e);
            return CompletableFuture.failedFuture(
                    new PostScheduledProcessingException("Failed to start scheduled posts processing", e));
        } finally {
            isRunning.set(false);
        }
    }

    private boolean canStartPublishing() {
        return isRunning.compareAndSet(false, true);
    }

    @Transactional(readOnly = true)
    private List<Post> fetchPostsToPublish() {
        return postRepository.findReadyToPublish();
    }

    private List<List<Post>> prepareChunks(List<Post> posts) {
        int chunkCount = Math.min(POST_PUBLISH_POOL_SIZE, posts.size());
        int chunkSize = (int) Math.ceil((double) posts.size() / chunkCount);
        return splitIntoChunks(posts, chunkSize);
    }

    private CompletableFuture<Void> processPublishPostChunks(List<List<Post>> chunks) {
        List<CompletableFuture<Void>> futures = chunks.stream()
                .map(chunk -> CompletableFuture
                        .runAsync(() -> {
                            TransactionTemplate transactionTemplate =
                                    new TransactionTemplate(transactionManager);
                            transactionTemplate.setPropagationBehavior(
                                    TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                            transactionTemplate.execute(status -> {
                                try {
                                    log.info("Processing chunk of size {}", chunk.size());
                                    chunk.forEach(post -> {
                                        post.setPublished(true);
                                        post.setPublishedAt(LocalDateTime.now());
                                        post.setScheduledAt(null);
                                    });
                                    postRepository.saveAll(chunk);
                                    log.info("Saved chunk of size {}", chunk.size());
                                } catch (Exception e) {
                                    log.error("Error processing chunk", e);
                                    throw e;
                                }
                                return null;
                            });
                        }, postPublishPool)
                        .exceptionally(throwable -> {
                            log.error("Chunk processing failed", throwable);
                            return null;
                        }))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to process all chunks", throwable);
                    } else {
                        log.info("All chunks processed successfully");
                    }
                });
    }

    private List<List<Post>> splitIntoChunks(List<Post> list, int chunkSize) {
        return ListUtils.partition(list, chunkSize);
    }

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
    public PostDto publishPost(PostDto postDto) {
        Post post = validateDataForPublication(postDto);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        post = postRepository.saveAndFlush(post);
        postEventBatchSender.dispatchEventsForPost(post);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
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
        long authorId = postDto.getAuthorId();
        return processPosts(postRepository.findByAuthorId(authorId),
                post -> !post.isPublished() && !post.isDeleted());
    }

    @Override
    public List<PostDto> getProjectPostDrafts(PostDto postDto) {
        long projectId = postDto.getProjectId();
        return processPosts(postRepository.findByProjectId(projectId),
                post -> !post.isPublished() && !post.isDeleted());
    }

    @Override
    public List<PostDto> getAuthorPublishedPosts(PostDto postDto) {
        long authorId = postDto.getAuthorId();
        return processPosts(postRepository.findByAuthorId(authorId),
                post -> post.isPublished() && !post.isDeleted());
    }

    @Override
    public List<PostDto> getProjectPublishedPosts(PostDto postDto) {
        long projectId = postDto.getProjectId();
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

    @Transactional(readOnly = true)
    public void correctUnpublishedPosts() {
        List<Post> unpublishedPosts = postRepository.findReadyToPublish();
        log.info("Starting correction for {} unpublished posts", unpublishedPosts.size());
        if (unpublishedPosts.isEmpty()) {
            return;
        }

        List<CompletableFuture<Post>> futures = unpublishedPosts.stream()
                .map(postCorrectService::correctPost)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .orTimeout(PostServiceConstants.CORRECT_POSTS_FUTURES_TIMEOUT, TimeUnit.SECONDS)
                .thenRun(() -> log.info("Finished correcting unpublished posts"))
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Correction unpublished posts process failed", throwable);
                    }
                })
                .join();
    }
}
