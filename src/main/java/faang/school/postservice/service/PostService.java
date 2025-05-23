package faang.school.postservice.service;

import faang.school.postservice.client.HashtagServiceClient;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.PostResponseDto;
import faang.school.postservice.dto.event.HashtagAddingEvent;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.entity.CachedPost;
import faang.school.postservice.exception.AsyncPostProcessingException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.HashtagServiceConnectionException;
import faang.school.postservice.exception.PostAlreadyPublishedException;
import faang.school.postservice.exception.PostUnverifiedException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.VerifiedStatus;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.publisher.HashtagAddingEventPublisher;
import faang.school.postservice.publisher.HashtagRemovingEventPublisher;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    private final HashtagAddingEventPublisher hashtagAddingPublisher;
    private final HashtagRemovingEventPublisher hashtagRemovingPublisher;
    private final HashtagServiceClient hashtagClient;
    private final PostCacheService postCacheService;

    @Value("${batch.size}")
    private int batchSize;

    @Value("${thread-pool.publish-timeout}")
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
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(threadTimeout, TimeUnit.MINUTES);
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
        log.info("Опубликовано {} постов с {} по {} id.", batch.size(), batch.get(0).getId(), batch.get(batch.size() - 1).getId());
    }

    public PostResponseDto create(PostDto postDto) {
        validateContent(postDto);
        validateAuthor(postDto.authorId(), postDto.projectId());
        Post post = postMapper.toEntity(postDto);
        Ad ad = null;
        if (post.getAd() != null) {
            ad = adRepository.findById(postDto.adId()).orElseThrow(() -> new RuntimeException("ad not found"));
        }
        List<Comment> comments = commentRepository.findByIdIn(postDto.commentsId() != null ? postDto.commentsId() : List.of());
        List<Like> likes = likeRepository.findByIdIn(postDto.likesId() != null ? postDto.likesId() : List.of());
        List<Resource> resources = resourceRepository.findByIdIn(postDto.resourcesId() != null ? postDto.resourcesId() : List.of());
        List<Album> albums = albumRepository.findByIdIn(postDto.albumsId() != null ? postDto.albumsId() : List.of());

        post.setAd(ad);
        post.setComments(comments);
        post.setLikes(likes);
        post.setResources(resources);
        post.setAlbums(albums);
        post.setVerifiedStatus(VerifiedStatus.PENDING);

        postRepository.save(post);
        log.info("Post created: {}", post);

        if (postDto.hashtagsName() != null && !postDto.hashtagsName().isEmpty()) {
            postDto.hashtagsName().forEach(hashtag -> {
                Long authorId = 0L;
                if (post.getAuthorId() != null) {
                    authorId = post.getAuthorId();
                }
                hashtagAddingPublisher.publish(takeHashtagEvent(post.getId(), hashtag, authorId));
            });
        }
        return postMapper.toResponseDto(post);
    }

    public PostResponseDto publish(Long postId) {
        Post post = takePost(postId);
        if (post.isPublished()) {
            throw new PostAlreadyPublishedException("Post with ID " + postId + " is already published.");
        }
        if (post.getVerifiedStatus() != VerifiedStatus.APPROVED) {
            throw new PostUnverifiedException("Post with id %d is unverified.", postId);
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);

        CachedPost cachedPost = buildCachedPost(post);
        postCacheService.cachePost(cachedPost);
        log.info("Пост {} отправлен в кеш", post.getId());
        log.info("Post published: {}", post);

        return postMapper.toResponseDto(post);
    }

    public PostResponseDto update(PostDto postDto, Long postId) {
        validateContent(postDto);
        Post post = takePost(postId);
        post.setContent(postDto.content());
        postRepository.save(post);
        log.info("Post updated: {}", post);
        return postMapper.toResponseDto(post);
    }

    public void deleteById(Long postId) {
        Post post = takePost(postId);
        post.setDeleted(true);
        post.setPublished(false);
        postRepository.save(post);
        log.info("Post deleted: {}", post);
        hashtagRemovingPublisher.publish(postId);
    }

    public PostResponseDto getPost(Long postId, Long userId) {
        Post post = takePost(postId);
        log.info("Post retrieved: {}", post);
        postViewEventPublisher.published(new PostViewEvent(postId, userId, post.getAuthorId(), LocalDateTime.now()));
        PostResponseDto response = postMapper.toResponseDto(post);
        List<Long> hashtags = hashtagClient.getHashtagsIdsByPostId(postId);
        response.setHashtagsId(hashtags);
        return response;
    }

    public List<PostResponseDto> findDraftsByAuthorId(Long authorId, Long userId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        Map<Long, List<Long>> hashtagsOnPosts = findHashtagsByPosts(posts);

        posts = posts.stream().filter(post -> !post.isDeleted() && !post.isPublished()).toList();
        return returnPostsDtoList(posts, userId, authorId, hashtagsOnPosts);
    }

    public List<PostResponseDto> findDraftsByProjectId(Long projectId, Long userId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        Map<Long, List<Long>> hashtagsOnPosts = findHashtagsByPosts(posts);

        posts = posts.stream().filter(post -> !post.isDeleted() && !post.isPublished()).toList();
        return returnPostsDtoList(posts, userId, projectId, hashtagsOnPosts);
    }

    public List<PostResponseDto> findPublishedByAuthorId(Long authorId, Long userId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        Map<Long, List<Long>> hashtagsOnPosts = findHashtagsByPosts(posts);

        posts = posts.stream().filter(post -> !post.isDeleted() && post.isPublished()).toList();
        return returnPostsDtoList(posts, userId, authorId, hashtagsOnPosts);
    }

    public List<PostResponseDto> findPublishedByProjectId(Long projectId, Long userId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        Map<Long, List<Long>> hashtagsOnPosts = findHashtagsByPosts(posts);

        posts = posts.stream().filter(post -> !post.isDeleted() && post.isPublished()).toList();
        return returnPostsDtoList(posts, userId, projectId, hashtagsOnPosts);
    }

    public List<PostResponseDto> getPostsByIds(List<Long> postIds) {
        return postMapper.toResponseDtoList(postRepository.findAllByIdIn(postIds));
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
        return postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    private List<List<Post>> partitionList(List<Post> list, int batchSize) {
        return IntStream.range(0, (list.size() + batchSize - 1) / batchSize).mapToObj(i -> list.subList(i * batchSize, Math.min((i + 1) * batchSize, list.size()))).toList();
    }

    private HashtagAddingEvent takeHashtagEvent(Long postId, String name, Long authorId) {
        return HashtagAddingEvent.builder().hashtagName(name).postId(postId).authorId(authorId).build();
    }

    private Map<Long, List<Long>> findHashtagsByPosts(List<Post> posts) {
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        Map<Long, List<Long>> hashtags;
        try {
            hashtags = hashtagClient.getHashtagsIdsByPostIds(postIds);
        } catch (FeignException e) {
            throw new HashtagServiceConnectionException(e.getMessage());
        }
        return hashtags;
    }

    private List<PostResponseDto> returnPostsDtoList(List<Post> posts, Long userId, Long id, Map<Long, List<Long>> hashtags) {
        return posts.stream().sorted(Comparator.comparing(Post::getPublishedAt).reversed()).peek(post -> postViewEventPublisher.published(new PostViewEvent(post.getId(), userId, id, LocalDateTime.now()))).map(postMapper::toResponseDto).peek(postDto -> postDto.setHashtagsId(hashtags.get(postDto.getId()))).toList();
    }

    private CachedPost buildCachedPost(Post post) {
        return CachedPost.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .publishedAt(Instant.from(post.getPublishedAt()))
                .content(post.getContent())
                .build();
    }

}
