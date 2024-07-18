package faang.school.postservice.validator;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidator {
    private final PostRepository postRepository;

    public Post validatePostExistence(long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            String errMessage = String.format("Post with ID: %d was not found in Database", postId);
            log.error(errMessage);
            throw new EntityNotFoundException(errMessage);
        }
        return postOptional.get();
    }
}