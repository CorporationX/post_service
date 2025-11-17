package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.post.PostV2CreateDto;
import faang.school.postservice.dto.post.PostV2Dto;
import faang.school.postservice.dto.post.PostV2UpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.PostV2Mapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.spec.PostSpecification;
import faang.school.postservice.service.ai.AiPostCorrectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.Collections;


@Slf4j
@RequiredArgsConstructor
@Service
public class PostV2Service {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final AiPostCorrectionService aiPostCorrectionService;

    private final Executor executor = Executors.newFixedThreadPool(10);

    @Transactional
    public PostV2Dto createPostAsDraft(PostV2CreateDto postV2CreateDto) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);

        Post post = PostV2Mapper.toEntity(postV2CreateDto);
        post.setAuthorId(userId);

        Post savedPost = postRepository.save(post);
        return PostV2Mapper.toDto(savedPost, 0L, Collections.emptyList());
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
        return PostV2Mapper.toDto(saved, 0L, Collections.emptyList());
    }

    @Transactional
    public PostV2Dto updatePost(Long postId, PostV2UpdateDto postV2UpdateDto) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);
        Post post = postRepository.findPostWithLikesOrThrow(postId);

        validatePostOwner(post, userId);
        PostV2Mapper.update(post, postV2UpdateDto);

        Post saved = postRepository.save(post);
        List<Long> likesIds = saved.getLikes().stream()
                .map(Like::getId)
                .toList();
        Long likesCount = (long) likesIds.size();
        return PostV2Mapper.toDto(saved, likesCount, likesIds);
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

    public PageResponse<PostV2Dto> findAllPublishedByFilter(Long authorId, Pageable pageable) {
        Specification<Post> spec = PostSpecification.filter(authorId, true);
        Page<Post> page = postRepository.findAll(spec, pageable);

        return PageResponse.from(page, post -> PostV2Mapper.toDto(post,
                (long) post.getLikes().size(),
                post.getLikes().stream()
                        .map(Like::getId)
                        .toList()
                )
        );
    }

    public PageResponse<PostV2Dto> findAllDraftsByAuthor(Pageable pageable) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);

        Specification<Post> spec = PostSpecification.filter(userId, false);
        Page<Post> page = postRepository.findAll(spec, pageable);

        return PageResponse.from(page, post -> PostV2Mapper.toDto(post,
                        (long) post.getLikes().size(),
                        post.getLikes().stream()
                                .map(Like::getId)
                                .toList()
                )
        );
    }

    public PostV2Dto findById(Long postId) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);
        Post post = postRepository.findPostWithLikesOrThrow(postId);
        List<Long> likesIds = post.getLikes().stream()
                .map(Like::getId)
                .toList();
        Long likesCount = (long) likesIds.size();
        return PostV2Mapper.toDto(post, likesCount, likesIds);
    }

    private void validatePostOwner(Post post, long userId) {
        if (post.getAuthorId() != null) {
            if (!post.getAuthorId().equals(userId)) {
                throw new ForbiddenException("User id=%s is not author of post".formatted(userId));
            }
        }
    }

    @Transactional
    public void correctDraftPosts() {
        List<Post> posts = postRepository.findAllForAiEditingWithLock();

        List<CompletableFuture<Void>> futures = posts.stream()
                .map(post -> CompletableFuture.runAsync(() -> aiPostCorrectionService.correctPost(post), executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}
