package faang.school.postservice.validator;

import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        if (authorId != userId){
            throw new DataValidationException("You are not the author of this post");
        }
    }
}
