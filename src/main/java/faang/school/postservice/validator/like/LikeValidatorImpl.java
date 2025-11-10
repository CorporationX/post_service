package faang.school.postservice.validator.like;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeValidatorImpl implements LikeValidator {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public Post validateLikeOnPost(long postId, long userId, boolean doSet) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()){
            log.error("Post doesn't exist");
            throw new ResourceNotFoundException("Post doesn't exist");
        }
        Optional<Like> like = likeRepository.findByPostIdAndUserId(postId, userId);
        if (like.isPresent() && doSet) {
            log.error("User has already set like on this post");
            throw new DataValidationException("User has already set a like on this post");
        }
        if (like.isEmpty() && !doSet) {
            log.error("User hasn't set like on this post");
            throw new DataValidationException("User hasn't set like on this post");
        }
        return post.get();
    }

    @Override
    public Comment validateLikeOnComment(long commentId, long userId, boolean doSet) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if(comment.isEmpty()) {
            log.error("Comment doesn't exist");
            throw new ResourceNotFoundException("Comment doesn't exist");
        }
        Optional<Like> like = likeRepository.findByCommentIdAndUserId(commentId, userId);
        if (like.isPresent() && doSet) {
            log.error("User has already set a like on the comment");
            throw new DataValidationException("User has already set a like on the comment");
        }
        if (like.isEmpty() && !doSet) {
            log.error("User hasn't set a like on the comment");
            throw new DataValidationException("User hasn't set a like on the comment");
        }
        return comment.get();
    }
}
