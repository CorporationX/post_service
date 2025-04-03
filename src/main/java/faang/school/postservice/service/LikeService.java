package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.like.LikeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class LikeService {
    private PostRepository postRepository;
    private LikeRepository likeRepository;
    private CommentRepository commentRepository;
    private UserServiceClient userServiceClient;
    private final LikeEventPublisher likeEventPublisher;

    private static final String ERROR_POST_DOES_NOT_EXIST = "Post doesn't exist: postId={}";
    private static final String ERROR_COMMENT_DOES_NOT_EXIST = "Comment doesn't exist: commentId={} ";
    private static final String ERROR_COMMENT_DOES_NOT_BELONG_TO_POST = "Comment doesn't belong to post: commentId={}; postId={}";
    private static final String USER_LIKES_POST = "User likes post: userId={} ; postId={}";
    private static final String USER_LIKES_COMMENT = "User likes comment: userId={}, commentId={}, postId={}";
    private static final String USER_TAKES_LIKE_AWAY_FROM_POST = "User takes like away from post: userId={} with postId={}";
    private static final String USER_TAKES_LIKE_AWAY_FROM_COMMENT = "User takes like away from comment: userId={}, commentId={}, postId={}";
    private static final String WARN_USER_SETS_CURRENT_LIKE_STATUS = "User like action repeats current like status: userId={}, commentId={}, postId={}";
    private static final String ERROR_USER_IS_NOT_PRESENTED_IN_DB = "User is not presented in DB: userId={}";

    public Like setLikeToPost(Like like) {
        Post post = getPost(like.getPost().getId());
        userIsPresentedInDataBase(like.getUserId());

        if (!likeOfPostIsAlreadyPresented(post, like.getUserId())) {
            like.setComment(null);
            Like likeFromDataBase = likeRepository.save(like);
            log.info(USER_LIKES_POST, like.getUserId(), like.getPost().getId());
            likeEventPublisher.publish(new LikePostEvent(post.getId(), post.getAuthorId(), like.getUserId()), "like_topic");
            return likeFromDataBase;
        } else {
            logWarningSameLikeStatus(like);
        }
        return new Like();
    }

    public Like unsetLikeToPost(Like like) {
        Post post = getPost(like.getPost().getId());
        userIsPresentedInDataBase(like.getUserId());

        if (likeOfPostIsAlreadyPresented(post, like.getUserId())) {
            Like likeToDelete = getPostLikeToDelete(post, like.getUserId());
            likeRepository.deleteById(likeToDelete.getId());
            like.setId(like.getId());
            log.info(USER_TAKES_LIKE_AWAY_FROM_POST, like.getUserId(), like.getPost().getId());
        } else {
            logWarningSameLikeStatus(like);
        }
        return like;
    }

    public Like setLikeToComment(Like like) {
        Comment comment = getComment(like.getComment().getId(), like.getPost().getId());
        userIsPresentedInDataBase(like.getUserId());

        if (!likeOfCommentIsAlreadyPresented(comment, like.getUserId())) {
            Like likeFromDataBase = likeRepository.save(like);
            log.info(USER_LIKES_COMMENT, like.getUserId(), like.getComment().getId(), like.getPost().getId());
            return likeFromDataBase;
        } else {
            logWarningSameLikeStatus(like);
        }
        return new Like();
    }

    public Like unsetLikeToComment(Like like) {
        Comment comment = getComment(like.getComment().getId(), like.getPost().getId());
        userIsPresentedInDataBase(like.getUserId());

        if (likeOfCommentIsAlreadyPresented(comment, like.getUserId())) {
            Like likeToDelete = getCommentLikeToDelete(comment, like.getUserId());
            likeRepository.deleteById(likeToDelete.getId());
            like.setId(likeToDelete.getId());
            log.info(USER_TAKES_LIKE_AWAY_FROM_COMMENT, like.getUserId(), like.getComment().getId(), like.getPost().getId());
        } else {
            logWarningSameLikeStatus(like);
        }
        return like;
    }

    public Post getNumberOfPostLikes(Long postId) {
        return getPost(postId);
    }

    private Like getPostLikeToDelete(Post post, Long userId) {
        Optional<Like> optionalLike = post.getLikes().stream()
                .filter(l -> l.getUserId().equals(userId))
                .findFirst();
        Like likeToDelete = new Like();
        if (optionalLike.isPresent()) {
            likeToDelete = optionalLike.get();
        }
        return likeToDelete;
    }

    private Like getCommentLikeToDelete(Comment comment, Long userId) {
        Optional<Like> optionalLike = comment.getLikes().stream()
                .filter(l -> l.getUserId().equals(userId))
                .findFirst();
        Like likeToDelete = new Like();
        if (optionalLike.isPresent()) {
            likeToDelete = optionalLike.get();
        }
        return likeToDelete;
    }

    private Post getPost(Long postId) {
        Optional<Post> optional = postRepository.findById(postId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            log.error(ERROR_POST_DOES_NOT_EXIST, postId);
            throw new DataValidationException(ERROR_POST_DOES_NOT_EXIST);
        }
    }

    private boolean likeOfPostIsAlreadyPresented(Post post, Long userId) {
        List<Like> likes = post.getLikes().stream()
                .filter(l -> l.getUserId().equals(userId))
                .toList();
        return !likes.isEmpty();
    }

    private boolean likeOfCommentIsAlreadyPresented(Comment comment, Long userId) {
        List<Like> likes = comment.getLikes().stream()
                .filter(l -> l.getUserId().equals(userId))
                .toList();
        return !likes.isEmpty();
    }

    private Comment getComment(Long commentId, Long postId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            if (comment.getPost().getId().equals(postId)) {
                return comment;
            } else {
                log.error(ERROR_COMMENT_DOES_NOT_BELONG_TO_POST, commentId, postId);
                throw new DataValidationException(ERROR_COMMENT_DOES_NOT_BELONG_TO_POST);
            }
        } else {
            log.error(ERROR_COMMENT_DOES_NOT_EXIST, postId);
            throw new DataValidationException(ERROR_POST_DOES_NOT_EXIST);
        }
    }

    private void userIsPresentedInDataBase(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        if (!userDto.id().equals(userId)) {
            log.error(ERROR_USER_IS_NOT_PRESENTED_IN_DB, userId);
            throw new DataValidationException(ERROR_USER_IS_NOT_PRESENTED_IN_DB);
        }
    }

    private void logWarningSameLikeStatus(Like like) {
        log.warn(WARN_USER_SETS_CURRENT_LIKE_STATUS, like.getUserId(), like.getComment().getId(), like.getPost().getId());
    }
}
