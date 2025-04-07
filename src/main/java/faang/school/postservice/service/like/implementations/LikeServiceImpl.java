package faang.school.postservice.service.like.implementations;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.LikeAlreadyExistException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.like.interfaces.LikeService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserContext userContext;

    private void checkLikeExistence(long entityId, long userId,
                                    BiFunction<Long, Long, Optional<Like>> findLikeEntityFunction,
                                    String entityName) {
        findLikeEntityFunction.apply(entityId, userId)
                .ifPresent(like -> {
                    throw new LikeAlreadyExistException(String.format("Like already exist: %sId=%d, userId=%d",
                            entityName, entityId, userId));
                });
    }

    private void removeLike(long entityId, long userId,
                            BiFunction<Long, Long, Optional<Like>> findLikeEntityFunction,
                            String entityName) {
        Like like = findLikeEntityFunction.apply(entityId, userId)
                .orElseThrow(() ->
                        new LikeNotFoundException(String.format("Like not found: %sId=%d, userId=%d",
                                entityName, entityId, userId)));
        likeRepository.delete(like);
    }

    @Override
    @Transactional
    public LikeDto likePost(long postId) {
        Post post = checkPostId(postId);
        long userId = userContext.getUserId();
        checkAuthor(userId);
        checkLikeExistence(postId, userId, likeRepository::findByPostIdAndUserId, "post");
        Like like = Like.builder().userId(userId).post(post).build();
        return likeMapper.toDto(likeRepository.save(like));
    }

    @Override
    @Transactional
    public void unlikePost(long postId) {
        removeLike(postId, userContext.getUserId(), likeRepository::findByPostIdAndUserId, "post");
    }

    @Override
    @Transactional
    public LikeDto likeComment(long commentId) {
        Comment comment = checkCommentId(commentId);
        long userId = userContext.getUserId();
        checkAuthor(userId);
        checkLikeExistence(commentId, userId, likeRepository::findByCommentIdAndUserId, "comment");

        Like like = Like.builder().userId(userId).comment(comment).build();
        return likeMapper.toDto(likeRepository.save(like));
    }

    @Override
    @Transactional
    public void unlikeComment(long commentId) {
        removeLike(commentId, userContext.getUserId(), likeRepository::findByCommentIdAndUserId, "comment");
    }

    private <T> T checkEntityId(long entityId,
                                CrudRepository<T, Long> repository,
                                Predicate<T> isNotDeleted,
                                Supplier<RuntimeException> exceptionSupplier) {
        return repository.findById(entityId).filter(isNotDeleted)
                .orElseThrow(exceptionSupplier);
    }

    private Post checkPostId(long postId) {
        return checkEntityId(postId, postRepository, p -> !p.isDeleted(),
                () -> new PostNotFoundException(String.format("Post not found: postId=%d", postId)));
    }

    private Comment checkCommentId(long commentId) {
        return checkEntityId(commentId, commentRepository, c -> !c.getPost().isDeleted(),
                () -> new CommentNotFoundException(String.format("Comment not found: commentId=%d", commentId)));
    }

    private void checkAuthor(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            int statusCode = e.status();
            log.error("Error while fetching author: id={}, status={}, message={}",
                    userId, statusCode, e.getMessage(), e);
            switch (statusCode) {
                case 404:
                    throw new AuthorNotFoundException("Author with id " + userId + " not found");
                case 400:
                    throw new IllegalArgumentException("Invalid author id: " + userId);
                case 500:
                    throw new RuntimeException("User service is unavailable. Try again later.");
                default:
                    throw new RuntimeException("Failed to fetch author: id=" + userId);
            }
        }
    }
}
