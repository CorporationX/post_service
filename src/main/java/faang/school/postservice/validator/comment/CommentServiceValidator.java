package faang.school.postservice.validator.comment;

import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentServiceValidator {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void validatePostExist(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchElementException("Post with id " + postId + " does not exist");
        }
    }

    public void validateCommentContent(String content) {
        if (content.isEmpty() || content.length() > 4096) {
            throw new IllegalArgumentException("Comment content is too long or content is empty");
        }
    }

    public void validateCommentExist(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NoSuchElementException("Comment with id " + commentId + " does not exist");
        }
    }
}
