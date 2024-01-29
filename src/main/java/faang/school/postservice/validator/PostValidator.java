package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validatePostContent(PostDto postDto) {
        String postContent = postDto.getContent();
        if (postContent.isEmpty() || postContent.isBlank()) {
            throw new DataValidationException("Post content cannot be empty");
        }
    }

    public void validateOwnerPost(PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("Post cannot belong to both author and project");
        }
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("Post must belong to either author or project");
        }
    }

    public void validateAuthor(long authorId) {
        userServiceClient.getUser(authorId);
    }

    public void validateProject(long projectId) {
        projectServiceClient.getProject(projectId);
    }
}
