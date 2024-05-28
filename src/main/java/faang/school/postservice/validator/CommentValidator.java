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

    public void getAllCommentsOnPostIdService(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new DataValidationException("Post not found with id: " + postId);
        }
    }
}