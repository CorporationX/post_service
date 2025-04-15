package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exceptions.AsyncPostProcessingException;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.exceptions.PostAlreadyPublishedException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.repository.ad.AdRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final ResourceRepository resourceRepository;
    private final AlbumRepository albumRepository;
    private final PostViewEventPublisher postViewEventPublisher;
    private final ExecutorService executorService;

    @Value("$.{batch.size}")
    private int batchSize;

    @Value("$.{thread-pool.publish-timeout}")
    private int threadTimeout;

    public void publishScheduledPosts() {
        List<Post> readyPosts = postRepository.findReadyToPublish();
        if (readyPosts.isEmpty()) {
            log.info("Нет постов для публикации.");
            return;
        }

        List<List<Post>> batches = partitionList(readyPosts, batchSize);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (List<Post> batch : batches) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    publishBatch(batch);
                } catch (DataValidationException e) {
                    log.error("Ошибка валидации в списке: {}", e.getMessage());
                    throw new DataValidationException("Ошибка валидации в списке. Размер: {}", batch.size());
                } catch (Exception e) {
                    log.error("Ошибка публикации списка: {}", e.getMessage());
                    throw new AsyncPostProcessingException("Не удалось опубликовать список", e);
                }
            }, executorService);

            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(threadTimeout, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            log.warn("Превышено время ожидания публикации");
            throw new AsyncPostProcessingException("Таймаут публикации постов", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Поток был прерван во время публикации");
            throw new AsyncPostProcessingException("Публикация прервана", e);
        } catch (ExecutionException e) {
            log.error("Ошибка при выполнении публикации", e.getCause());
            throw new AsyncPostProcessingException("Ошибка публикации постов", e.getCause());
        }
    }

    public PostDto create(PostDto postDto) {
        validateContent(postDto);
        validateAuthor(postDto.authorId(), postDto.projectId());
        Post post = postMapper.toEntity(postDto);
        Ad ad = adRepository.findById(postDto.adId()).orElseThrow(
                () -> new RuntimeException("ad not found"));
        List<Comment> comments = commentRepository.findByIdIn(postDto
                .commentsId() != null ? postDto.commentsId() : List.of());
        List<Like> likes = likeRepository.findByIdIn(postDto
                .likesId() != null ? postDto.likesId() : List.of());
        List<Resource> resources = resourceRepository.findByIdIn(postDto
                .resourcesId() != null ? postDto.resourcesId() : List.of());
        List<Album> albums = albumRepository.findByIdIn(postDto
                .albumsId() != null ? postDto.albumsId() : List.of());

        post.setAd(ad);
        post.setComments(comments);
        post.setLikes(likes);
        post.setResources(resources);
        post.setAlbums(albums);

        postRepository.save(post);
        log.info("Post created: {}", post);
        return postMapper.toDto(post);
    }

    public PostDto publish(Long postId) {
        Post post = takePost(postId);
        if (post.isPublished()) {
            throw new PostAlreadyPublishedException("Post with ID " + postId + " is already published.");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        log.info("Post published: {}", post);
        return postMapper.toDto(post);
    }

    public PostDto update(PostDto postDto, Long postId) {
        validateContent(postDto);
        Post post = takePost(postId);
        post.setContent(postDto.content());
        postRepository.save(post);
        log.info("Post updated: {}", post);
        return postMapper.toDto(post);
    }

    public void deleteById(Long postId) {
        Post post = takePost(postId);
        post.setDeleted(true);
        post.setPublished(false);
        log.info("Post deleted: {}", post);
        postRepository.save(post);
    }

    public PostDto getPost(Long postId, Long userId) {
        Post post = takePost(postId);
        log.info("Post retrieved: {}", post);
        postViewEventPublisher.published(new PostViewEvent(postId, userId,
                post.getAuthorId(), LocalDateTime.now()));
        return postMapper.toDto(post);
    }

    public List<PostDto> findDraftsByAuthorId(Long authorId, Long userId) {
        return postRepository.findByAuthorId(authorId)
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .peek(post -> {
                    postViewEventPublisher.published(
                            new PostViewEvent(post.getId(), userId, authorId, LocalDateTime.now()));
                })
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> findDraftsByProjectId(Long projectId, Long userId) {
        return postRepository.findByProjectId(projectId)
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .peek(post -> {
                    postViewEventPublisher.published(
                            new PostViewEvent(post.getId(), userId, projectId, LocalDateTime.now()));
                })
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> findPublishedByAuthorId(Long authorId, Long userId) {
        return postRepository.findByAuthorId(authorId)
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .peek(post -> {
                    postViewEventPublisher.published(
                            new PostViewEvent(post.getId(), userId, authorId, LocalDateTime.now()));
                })
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> findPublishedByProjectId(Long projectId, Long userId) {
        return postRepository.findByProjectId(projectId)
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .peek(post -> {
                    postViewEventPublisher.published(
                            new PostViewEvent(post.getId(), userId, projectId, LocalDateTime.now()));
                })
                .map(postMapper::toDto)
                .toList();
    }

    private void validateContent(PostDto postDto) {
        if (postDto.content() == null || postDto.content().isBlank()) {
            throw new NullPointerException("Content is null or empty");
        }
    }

    private void validateAuthor(Long authorId, Long projectId) {
        boolean isProject = projectId != null;
        boolean isUser = authorId != null;

        if (isProject && isUser) {
            throw new IllegalArgumentException("Only one author must be specified: either the user or the project.");
        }

        if (isProject && !existsProject(projectId)) {
            throw new RuntimeException("Project with ID " + projectId + " does not exist.");
        }

        if (isUser && !existsUser(authorId)) {
            throw new RuntimeException("Author with ID " + authorId + " does not exist.");
        }
    }

    private boolean existsUser(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
            return true;
        } catch (FeignException e) {
            return false;
        }
    }

    private boolean existsProject(Long projectId) {
        try {
            projectServiceClient.getProject(projectId);
            return true;
        } catch (FeignException e) {
            return false;
        }
    }

    private Post takePost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post not found"));
    }

    private List<List<Post>> partitionList(List<Post> list, int batchSize) {
        return IntStream.range(0, (list.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> list.subList(
                        i * batchSize, Math.min((i + 1) * batchSize, list.size())
                ))
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishBatch(List<Post> batch) {
        if (batch == null || batch.isEmpty()) {
            log.error("в списке не содержится постов");
            throw new DataValidationException("список постов пуст");
        }

        for (Post post : batch) {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        }

        postRepository.saveAll(batch);
        log.info("Опубликовано {} постов с {} по {} id.",
                batch.size(), batch.get(0).getId(), batch.get(batch.size() - 1).getId());
    }

}


