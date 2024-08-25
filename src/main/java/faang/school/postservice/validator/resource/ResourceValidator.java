package faang.school.postservice.validator.resource;

import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceValidator {
    @Value("${services.s3.imageParameters.maxImagePerPost}")
    private int maxImagePerPost;
    private final ResourceRepository resourceRepository;

    public void validateResourceInPost(long postId, Resource resource) {
        long postIdFromResource = resource.getPost().getId();
        if (postId != postIdFromResource) {
            String errorMessage = "Resource ID = " + resource.getId() + " doesn't contain Post ID = " + postId;
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }
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
