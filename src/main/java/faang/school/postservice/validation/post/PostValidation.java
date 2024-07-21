package faang.school.postservice.validation.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidation {

    private final int MINIMUM_SIZE_OF_CONTENT = 3;

    public boolean isNullable(Object object) {
        return object == null;
    }


    public boolean isPublished(PostDto postDto) {
        return postDto.isPublished();
    }

    public boolean oneOfTheAuthorsIsNoNullable(Long authorId, Long projectId) {
        return (authorId != null || projectId != null) && !(authorId != null && projectId != null);
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
