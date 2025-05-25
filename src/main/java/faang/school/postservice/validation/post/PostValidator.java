package faang.school.postservice.validation.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.model.post.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void checkPost(Post post) {
        Long projectId = post.getProjectId();

        if (projectId == null) {
            userServiceClient.getCurrentUser();
        } else {
            projectServiceClient.getProject(projectId);
        }
    }

    public void checkPostIsNotPublished(Post post) {
        if (post.isPublished()) {
            log.error("Post with id {} already published", post.getId());
            throw new PostAlreadyPublishedException(post.getId());
        }
    }
}
