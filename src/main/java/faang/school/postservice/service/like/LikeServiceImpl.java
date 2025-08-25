package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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
        var post = getPostOrThrow(postId);

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
    }

    @Override
    @Transactional
    public void removeLikeFromPost(long postId) {
        var userId = getCurrentUserId();
        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new IllegalStateException("Like on post not found");
        }
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Override
    @Transactional
    public void addLikeToComment(long commentId) {
        var userId = getCurrentUserIdAndValidate();
        var comment = getCommentOrThrow(commentId);

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
    }

    @Override
    @Transactional
    public void removeLikeFromComment(long commentId) {
        var userId = getCurrentUserId();
        if (!likeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new IllegalStateException("Like on comment not found");
        }
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    private long getCurrentUserId() {
        return userContext.getUserId();
    }

    private long getCurrentUserIdAndValidate() {
        var userId = getCurrentUserId();
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("User not found");
        }
        return userId;
    }

    private Post getPostOrThrow(long postId) {
        return postRepository.getRequiredById(postId);
    }

    private Comment getCommentOrThrow(long commentId) {
        return commentRepository.getRequiredById(commentId);
    }

    @Override
    public List<UserDto> getPostLikers(long postId) {
        Post post = postRepository.getRequiredById(postId);

        List<Like> likes = Optional.ofNullable(post.getLikes())
                .orElseGet(() -> {
                    log.info("Post with id {} has no likes", postId);
                    return Collections.emptyList();
                });

        return likes.stream()
                .map(like -> userServiceClient.getUser(like.getUserId()))
                .toList();

    }

    @Override
    public List<UserDto> getCommentLikers(long commentId) {
        Comment comment = commentRepository.getRequiredById(commentId);

        List<Like> likes = Optional.ofNullable(comment.getLikes())
                .orElseGet(() -> {
                    log.info("Comment with id {} has no likes", commentId);
                    return Collections.emptyList();
                });

        return likes.stream()
                .map(like -> userServiceClient.getUser(like.getUserId()))
                .toList();
    }

}