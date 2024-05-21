package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final PostRepository postRepository;

    public void createCommentController(String content, Long authorId, Long postId) {
        if (content == null || content.isBlank()) {
            throw new DataValidationException("content can't be empty");
        }

        if (authorId == null) {
            throw new DataValidationException("authorId can't be empty");
        }

        if (postId == null) {
            throw new DataValidationException("postId can't be empty");
        }

        if (content.length() > 4096) {
            throw new DataValidationException("content can't be more 4096 symbols");
        }
    }

    public void changeCommentController(Long commentId, String content) {
        if (content == null || content.isBlank()) {
            throw new DataValidationException("content can't be empty");
        }

        if (commentId == null) {
            throw new DataValidationException("commentId can't be empty");
        }
    }

    public void getAllCommentsOnPostIdController(Long postId) {
        if (postId == null) {
            throw new DataValidationException("postId can't be empty");
        }
    }

    public void deleteCommentController(Long commentId) {
        if (commentId == null) {
            throw new DataValidationException("commentId can't be empty");
        }
    }

    public void getAllCommentsOnPostIdService(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new DataValidationException("Post not found with id: " + postId);
        }
    }
}