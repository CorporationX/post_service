package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidateService {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId should not be null");
        }

        userServiceClient.getUser(userId);
    }

    public void validatePost(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("postId should not be null");
        }

        projectServiceClient.getProject(postId);
    }
}
