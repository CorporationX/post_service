package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;

    public void validateUser(long userId) {
        userServiceClient.getUser(userId);
    }

    public void validateProject(long projectId) {
        projectServiceClient.getProject(projectId);
    }

    public void validatePostByUser(Post post, long userId) {
        if (post.getAuthorId() != userId) {
            throw new DataValidationException("You not author of this post");
        }
    }

    public void validatePostByProject(Post post, long projectId) {
        if (post.getProjectId() != projectId) {
            throw new DataValidationException("You not author of this post");
        }
    }

    public void validatePostToUpdate(Post post, PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            if (postDto.getAuthorId() != post.getAuthorId()) {
                throw new DataValidationException("You can't update this post");
            }
        }
        if (postDto.getProjectId() != null){
            if (post.getProjectId() != postDto.getProjectId()) {
                throw new DataValidationException("You can't update this post");
            }
        }
    }

    public void isPublished(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("This post is published");
        }
    }

    public void isDeleted(Post post) {
        if (post.isDeleted()) {
            throw new DataValidationException("This post is deleted");
        }
    }
}
