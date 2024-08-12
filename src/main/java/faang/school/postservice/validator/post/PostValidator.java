package faang.school.postservice.validator.post;

import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.validator.project.ProjectValidator;
import faang.school.postservice.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final int MINIMUM_SIZE_OF_CONTENT = 3;
    private final UserValidator userValidator;
    private final ProjectValidator projectValidator;

    public void checkPostPublished(Long id, boolean published) {
        if (published) {
            throw new DataValidationException(String.format("Post %s has already published", id));
        }
    }

    public void checkIfPostHasAuthor(Long authorId, Long projectId) {
        boolean result = (authorId != null) ^ (projectId != null);
        if (!result) {
            throw new DataValidationException("Only one of projectId or authorId must be provided");
        }

        if (authorId != null) {
            userValidator.checkUserExist(authorId);
        }

        if (projectId != null) {
            projectValidator.checkProjectExist(projectId);
        }
    }

    public void validateForUpdating(PostUpdateDto postDto) {
        if (postDto.getId() == null) {
            throw new DataValidationException("Id can't be empty");
        }

        if (postDto.getContent() == null) {
            throw new DataValidationException("Content can't be empty");
        }

        if (postDto.getContent().length() < MINIMUM_SIZE_OF_CONTENT) {
            throw new DataValidationException(String.format("Size of post content must contains minimum %s characters", MINIMUM_SIZE_OF_CONTENT));
        }
    }
}
