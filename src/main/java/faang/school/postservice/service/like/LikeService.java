package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.like.LikeNotFoundException;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.entity.like.Like;
import faang.school.postservice.entity.post.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.like.LikeValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeValidator likeValidator;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    @Transactional
    public Like addLikeToPost(long postId) {
        long userIdContext = userContext.getUserId();
        long userId = userServiceClient.getUserById(userIdContext).getId();
        Post post = postService.getPostById(postId);
        likeValidator.checkUserHasNoLikeOnPost(userId, postId);
        Like like = new Like();
        like.setUserId(userId);
        like.setPost(post);
        return likeRepository.save(like);
    }

    @Transactional
    public Like addLikeToComment(long commentId) {
        long userIdContext = userContext.getUserId();
        long userId = userServiceClient.getUserById(userIdContext).getId();
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
        likeRepository.findByPostIdAndUserId(postId, userId)
                        .ifPresentOrElse(
                                like -> likeRepository.deleteById(like.getId()),
                                () -> {
                                    log.error("User with id = {} has no like on the post with id = {}}", userId, postId);
                                    throw new LikeNotFoundException(String.format(
                                            "User with id = %d has no like on the post with id = %d", userId, postId));
                                }
                        );
    }

    @Transactional
    public void deleteLikeFromComment(long commentId) {
        long userId = userContext.getUserId();
        likeRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresentOrElse(
                        like -> likeRepository.deleteById(like.getId()),
                        () -> {
                            log.error("User with id = {} has no like on the comment with id = {}}", userId, commentId);
                            throw new LikeNotFoundException(String.format(
                                    "User with id = %d has no like on the comment with id = %d", userId, commentId));
                        }
                );
    }
}
