package faang.school.postservice.validator.resource;

import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ResourceValidator {
    @Value("${services.s3.imageParameters.maxImagePerPost}")
    private int maxImagePerPost;

    public int validateResourceInPost(Post post, long resourceId) {
        List<Resource> resourceList = post.getResources();
        List<Long> resourceIds = resourceList.stream()
                .map(Resource::getId).toList();
        if (!resourceIds.contains(resourceId)) {
            String errorMessage = "Post ID = " + post.getId() + " doesn't contain resource ID = " + resourceId;
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        return resourceIds.indexOf(resourceId);
    }

    public void validateLimitResourcesPerPost(Post post, int numberOfNewResources) {
        int currentResources = post.getResources().size();
        if ((currentResources + numberOfNewResources) > maxImagePerPost) {
            String errorMessage = "You are over the image limit in your post. Max image per post =  " + maxImagePerPost;
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }
    }
}
