package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.post.PostV2CreateDto;
import faang.school.postservice.dto.post.PostV2Dto;
import faang.school.postservice.dto.post.PostV2UpdateDto;
import faang.school.postservice.dto.post.PublishPostEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.PostV2Mapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.spec.PostSpecification;
import faang.school.postservice.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostV2Service {
    private static final Integer COUNT_THREADS_IN_EXECUTOR = 100;

    private ExecutorService executorService = Executors.newFixedThreadPool(COUNT_THREADS_IN_EXECUTOR);

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final RedisService redisService;

    @Transactional
    public PostV2Dto createPostAsDraft(PostV2CreateDto postV2CreateDto) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);

        Post post = PostV2Mapper.toEntity(postV2CreateDto);
        post.setAuthorId(userId);

        Post savedPost = postRepository.save(post);
        return PostV2Mapper.toDtoBasic(savedPost);
    }

    @Transactional
    public PostV2Dto publishPost(Long postId) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);
        Post post = postRepository.getByIdOrThrow(postId);

        if (post.isPublished()) {
            throw new ForbiddenException("Post id=%s is already published".formatted(post.getId()));
        }

        validatePostOwner(post, userId);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post saved = postRepository.save(post);
        log.info("Post {} published", postId);

        PostV2Dto postV2Dto = PostV2Mapper.toDtoBasic(saved);

        CompletableFuture.runAsync(() -> redisService.savePostInRedis(postV2Dto), executorService);
        return postV2Dto;
    }

    @Transactional
    public PostV2Dto updatePost(Long postId, PostV2UpdateDto postV2UpdateDto) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);
        Post post = postRepository.findPostWithLikesAndCommentOrThrow(postId);

        validatePostOwner(post, userId);
        PostV2Mapper.update(post, postV2UpdateDto);

        Post saved = postRepository.save(post);
        return PostV2Mapper.toDto(saved);
    }

    public void deletePostSoftly(Long postId) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);
        Post post = postRepository.getByIdOrThrow(postId);

        if (post.isDeleted()) {
            throw new ForbiddenException("Post id=%s is already deleted".formatted(post.getId()));
        }

        validatePostOwner(post, userId);

        post.setDeleted(true);
        postRepository.save(post);
    }

    @PublishPostEvent(eventClass = PageResponse.class)
    @Transactional(readOnly = true)
    public PageResponse<PostV2Dto> findAllPublishedByFilter(Long authorId, Pageable pageable) {
        Specification<Post> spec = PostSpecification.filter(authorId, true);
        Page<Post> page = postRepository.findAll(spec, pageable);

        return PageResponse.from(page, PostV2Mapper::toDto);
    }

    public PageResponse<PostV2Dto> findAllDraftsByAuthor(Pageable pageable) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);

        Specification<Post> spec = PostSpecification.filter(userId, false);
        Page<Post> page = postRepository.findAll(spec, pageable);

        return PageResponse.from(page, PostV2Mapper::toDto);

    }

    @PublishPostEvent(eventClass = PostV2Dto.class)
    @Transactional(readOnly = true)
    public PostV2Dto findById(Long postId) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);
        Post post = postRepository.findPostWithLikesAndCommentOrThrow(postId);
        return PostV2Mapper.toDto(post);
    }

    private void validatePostOwner(Post post, long userId) {
        if (post.getAuthorId() != null) {
            if (!post.getAuthorId().equals(userId)) {
                throw new ForbiddenException("User id=%s is not author of post".formatted(userId));
            }
        }
    }
}
