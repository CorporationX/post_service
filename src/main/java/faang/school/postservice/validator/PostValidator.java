package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostValidator {
    private final PostRepository postRepository;

    public void validateAuthorIdAndProjectId(Long authorId, Long projectId) {
        if ((authorId == null && projectId == null)
                || (authorId != null && projectId != null)) {
            throw new DataValidationException("Either authorId or projectId should be provided, but not both");
        }

        if (authorId != null && !postRepository.existsById(authorId)) {
            log.error("User with ID {} does not exist", authorId);
            throw new DataValidationException("User with Id " + authorId + " does not exist in the database");
        }

        if (projectId != null && !postRepository.existsById(projectId)) {
            log.error("Project with ID {} does not exist", projectId);
            throw new DataValidationException("Project with Id " + projectId + " does not exist in the database");
        }
    }

    public void validatePublicationPost(Post post) {
        if (post.isPublished()) {
            log.error("The post has already been published.");
            throw new DataValidationException("The post has already been published");
        }
    }
}
