package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.async.AsyncConfig;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.dto.post.ReadPostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.event.PostEvent;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.redis.PostEventPublisher;
import faang.school.postservice.publisher.kafka.PostEventProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.corrector.PostCorrector;
import faang.school.postservice.service.moderate.ModerationService;
import faang.school.postservice.validator.post.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final PostCorrector postCorrector;
    private final ModerationService moderationService;
    private final AsyncConfig asyncConfig;
    private final PostEventPublisher postEventPublisher;
    private final PostEventProducer postEventProducer;
    private final UserServiceClient userServiceClient;
    private final FeedService feedService;

    public Post findById(@NotNull Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(format("Пост с id=%d не найден", id)));
    }

    public List<ReadPostDto> getFilteredPosts(PostFilterDto postFilterDto) {
        postValidator.validateFilterDto(postFilterDto);

        if (postFilterDto.authorId() != null) {
            return getPosts(postFilterDto.authorId(), postFilterDto.isPublished(), true);
        } else {
            return getPosts(postFilterDto.projectId(), postFilterDto.isPublished(), false);
        }
    }

    private List<ReadPostDto> getPosts(Long id, boolean published, boolean byAuthor) {
        List<Post> posts = byAuthor ? postRepository.findByAuthorId(id) : postRepository.findByProjectId(id);

        posts = posts.stream()
                .filter(post -> post.isPublished() == published && !post.isDeleted())
                .sorted(Comparator.comparing(published ? Post::getPublishedAt : Post::getCreatedAt).reversed())
                .toList();

        return postMapper.toDtoList(posts);
    }

    @Transactional
    public ReadPostDto create(CreatePostDto createPostDto) {
        postValidator.validateDraftPost(createPostDto);

        Post post = postMapper.toEntity(createPostDto);
        post.setPublished(false);

        Post savedPost = postRepository.save(post);

        List<Long> subscriberIds = userServiceClient.getFollowerIds(savedPost.getAuthorId());
        PostCreatedEvent event = PostCreatedEvent.builder()
                .postId(savedPost.getId())
                .authorId(savedPost.getAuthorId())
                .subscriberIds(subscriberIds)
                .build();
        postEventProducer.sendEvent(event);

        return postMapper.toDto(savedPost);
    }

    public ReadPostDto getPost(long postId) {
        Post post = findById(postId);
        if (post.isDeleted()) {
            throw new EntityNotFoundException(format("Пост с id=%d не найден", postId));
        }
        return postMapper.toDto(post);
    }

    @Transactional
    public ReadPostDto update(long postId, UpdatePostDto updatePostDto) {
        Post post = findById(postId);

        post.setContent(updatePostDto.content());

        Post updatedPost = postRepository.save(post);

        return postMapper.toDto(updatedPost);
    }

    @Transactional
    public ReadPostDto delete(long id) {
        Post post = findById(id);
        postValidator.validateNotDeleted(post);
        post.setDeleted(true);
        Post updatedPost = postRepository.save(post);
        return postMapper.toDto(updatedPost);
    }

    @Transactional
    public ReadPostDto publish(long id) {
        Post post = findById(id);

        postValidator.validateNotPublished(post);
        postValidator.validateNotDeleted(post);
        postValidator.validatePostAuthorExist(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);
        PostEvent event = PostEvent.builder()
                .authorId(updatedPost.getAuthorId())
                .postId(updatedPost.getId())
                .build();
        postEventPublisher.publish(event);

        return postMapper.toDto(updatedPost);
    }

    public void correctAllUnpublishedPosts() {
        List<Post> posts = postRepository.findReadyToPublish();
        posts.forEach(postCorrector::correctContentPost);
        postRepository.saveAll(posts);
    }

    public void moderateUnverifiedPosts() {
        int page = 0;
        int batchSize = moderationService.getBatchSize();
        Executor executor = asyncConfig.taskExecutor();
        List<Post> posts;

        do {
            posts = moderationService.getUnverifiedPostsBatch(page);
            if (!posts.isEmpty()) {
                List<Post> finalPosts = posts;
                executor.execute(() -> CompletableFuture.runAsync(() -> moderationService.processPosts(finalPosts)));
            }
            page++;
        } while (posts.size() == batchSize);
    }

    @Transactional
    public void incrementView(long postId) {
        Post post = findById(postId);

        if (post.isPublished()) {
            post.setViews(post.getViews() + 1);
            feedService.incrementViewCache(postId);
        }
    }
}