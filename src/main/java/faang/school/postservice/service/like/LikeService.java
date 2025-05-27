package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.like.LikeValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeValidator likeValidator;
    private final UserContext userContext;

    @Transactional
    public Like addLikeToPost(long postId) {
        long userId = userContext.getUserId();
        likeValidator.checkLikeAuthorExists(userId);
        Post post = postService.getPostById(postId);
        likeValidator.checkUserHasNoLikeOnPost(userId, postId);
        Like like = new Like();
        like.setUserId(userId);
        like.setPost(post);
        return likeRepository.save(like);
    }

    @Transactional
    public Like addLikeToComment(long commentId) {
        long userId = userContext.getUserId();
        likeValidator.checkLikeAuthorExists(userId);
        Comment comment = commentService.get(commentId);
        likeValidator.checkUserHasNoLikeOnComment(userId, commentId);
        Like like = new Like();
        like.setUserId(userId);
        like.setComment(comment);
        return likeRepository.save(like);
    }

    @Transactional
    public void deleteLikeFromPost(long postId) {
        long userId = userContext.getUserId();
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Transactional
    public void deleteLikeFromComment(long commentId) {
        long userId = userContext.getUserId();
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }
}
