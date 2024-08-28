package faang.school.postservice.validator;

import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidator {
    private final PostRepository postRepository;

    public void validatePostExistence(long postId) {
        if (!postRepository.existsById(postId)) {
            String errMessage = String.format("Post with ID: %d was not found in Database", postId);
            log.error(errMessage);
            throw new EntityNotFoundException(errMessage);
        }
    }
}