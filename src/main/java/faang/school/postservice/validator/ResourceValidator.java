package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class ResourceValidator {

    public void validateResourceLimit(int count) {
        if (count >= 10) {
            throw new DataValidationException("Cannot upload more than 10 images");
        }
    }

    public void validateResourceBelongsToPost(Resource resource, long postId) {
        if (resource.getPost().getId() != postId) {
            throw new DataValidationException("Resource does not belong to post");
        }
    }

    public void validateUserIsPostAuthor(long authorId, long userId) {
        if (authorId != userId) {
            throw new DataValidationException("You are not the author of this post");
        }
    }

    public void validateResourceType(MultipartFile file) {
        String contentType = file.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg")) {
            throw new DataValidationException("Invalid file type");
        }
    }
}
