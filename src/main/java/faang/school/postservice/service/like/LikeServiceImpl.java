package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserContext userContext;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public void addLikeToPost(long postId) {
        var userId = getCurrentUserIdAndValidate();
        var post = postRepository.getRequiredById(postId);

        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new IllegalStateException("You already liked this post");
        }

        if (likeRepository.existsByUserIdAndPostIdOnComments(userId, postId)) {
            throw new IllegalStateException("You cannot like post and its comment simultaneously");
        }

        var like = new Like();
        like.setUserId(userId);
        like.setPost(post);
        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void removeLikeFromPost(long postId) {
        var userId = getCurrentUserIdAndValidate();
        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new IllegalStateException("Like on post not found");
        }
        likeRepository.deleteByPostIdAndUserId(postId, userId);

        var post = postRepository.getRequiredById(postId);
        post.setLikeCount(post.getLikeCount() - 1);
        postRepository.save(post);

    }

    @Override
    @Transactional
    public void addLikeToComment(long commentId) {
        var userId = getCurrentUserIdAndValidate();
        var comment = commentRepository.getRequiredById(commentId);

        if (likeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new IllegalStateException("You already liked this comment");
        }

        var postId = comment.getPost().getId();
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new IllegalStateException("You cannot like post and its comment simultaneously");
        }

        var like = new Like();
        like.setUserId(userId);
        like.setComment(comment);
        likeRepository.save(like);

        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void removeLikeFromComment(long commentId) {
        var userId = getCurrentUserIdAndValidate();
        if (!likeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new IllegalStateException("Like on comment not found");
        }
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);

        var comment = commentRepository.getRequiredById(commentId);
        comment.setLikeCount(comment.getLikeCount() - 1);
        commentRepository.save(comment);
    }

    private long getCurrentUserIdAndValidate() {
        var userId = userContext.getUserId();
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("User not found");
        }
        return userId;
    }
}