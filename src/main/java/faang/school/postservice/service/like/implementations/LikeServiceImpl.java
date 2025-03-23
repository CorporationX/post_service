package faang.school.postservice.service.like.implementations;

import faang.school.postservice.client.UserServiceClient;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * @param postId
     * @param userId
     * @return
     */
    @Override
    public LikeDto likePost(long postId, long userId) {
        Post post = checkPostId(postId);
        checkAuthor(userId);
        likeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(like -> {
                    throw new LikeAlreadyExistException(String.format("Like already exist: postId=%d, userId=%d",
                            postId, userId));
                });
        Like like = Like.builder().userId(userId).post(post).build();
        return likeMapper.toDto(likeRepository.save(like));
    }

    private Post checkLikeDToWithPostId(long postId, LikeDto likeDto) {
        if (postId != likeDto.getPostId()) {
            log.error("Post ID mismatch: path={}, dto={}", postId, likeDto.getPostId());
            throw new PostIdMismatchException("Post ID in path and DTO must match");
        }
        checkAuthor(likeDto.getUserId());
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
    }

    /**
     * @param postId
     * @param likeDto
     */
    private Post checkLikeDToWithPostId(long postId, LikeDto likeDto) {
        if (postId != likeDto.getPostId()) {
            log.error("Post ID mismatch: path={}, dto={}", postId, likeDto.getPostId());
            throw new PostIdMismatchException("Post ID in path and DTO must match");
        }
        checkAuthor(likeDto.getUserId());
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
    }

    @Override
    public void unlikePost(long postId, long userId) {
        Like like = likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() ->
                        new LikeNotFoundException(String.format("Like not found: postId=%d, userId=%d",
                                postId, userId)));
        likeRepository.delete(like);
    }

    /**
     * @param commentId
     * @param userId
     * @return
     */
    @Override
    public LikeDto likeComment(long commentId, long userId) {
        Comment comment = checkCommentId(commentId);
        checkAuthor(userId);
        likeRepository.findByCommentIdAndUserId(commentId, userId).ifPresent(like -> {
            throw new LikeAlreadyExistException(String.format("Like already exist: commentId=%d, userId=%d",
                    commentId, userId));
        });

        Like like = Like.builder().userId(userId).comment(comment).build();
        return likeMapper.toDto(likeRepository.save(like));
    }

    private Comment checkLikeDtoWithCommentId(long commentId, LikeDto likeDto) {
        if (commentId != likeDto.getCommentId()) {
            log.error("Comment ID mismatch: path={}, dto={}", commentId, likeDto.getCommentId());
            throw new CommentIdMismatchException("Comment ID in path and DTO must match");
        }
        checkAuthor(likeDto.getUserId());
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
    }

    /**
     * param commentId
     *
     * @param userId
     */
    @Override
    public void unlikeComment(long commentId, long userId) {
        Like like = likeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() ->
                        new LikeNotFoundException(String.format("Like not found: commentId=%d, userId=%d",
                                commentId, userId)));
        likeRepository.delete(like);
    }

    private <T> T checkEntityId(long entityId, CrudRepository<T, Long> repository, RuntimeException exception) {
        return repository.findById(entityId)
                .orElseThrow(() -> exception);
    }

    private Post checkPostId(long postId) {
        return checkEntityId(postId, postRepository,
                new PostNotFoundException(String.format("Post not found: postId=%d", postId)));
    }

    private Comment checkCommentId(long commentId) {
        return checkEntityId(commentId, commentRepository,
                new CommentNotFoundException(String.format("Comment not found: commentId=%d", commentId)));
    }

    private void checkAuthor(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            log.error("Author not found: id={}", userId, e);
            throw new AuthorNotFoundException("Author with id " + userId + " not found");
        }
    }
}
