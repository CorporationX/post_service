package faang.school.postservice.service.likes;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.AlreadyLikedException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserContext userContext;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public void createPostLike(Long postId) {
        long userId = userContext.getUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post " + postId + " not found"));

        likeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(like -> {
                    throw new AlreadyLikedException("User " + userId + " already liked post " + postId);
                });

        Like like = Like.builder()
                .post(post)
                .userId(userId)
                .build();

        likeRepository.save(like);
    }

    @Override
    public void deletePostLike(Long postId) {
        long userId = userContext.getUserId();
        Like like = likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new LikeNotFoundException("Like not found"));

        likeRepository.delete(like);
    }

    @Override
    public void createCommentLike(Long commentId) {
        long userId = userContext.getUserId();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment " + commentId + " not found"));

        likeRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresent(like -> {
                    throw new AlreadyLikedException("User " + userId + " already liked comment " + commentId);
                });

        Like like = Like.builder()
                .comment(comment)
                .userId(userId)
                .build();

        likeRepository.save(like);
    }

    @Override
    public void deleteCommentLike(Long commentId) {
        long userId = userContext.getUserId();
        Like like = likeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new LikeNotFoundException("Like not found"));

        likeRepository.delete(like);
    }
}
