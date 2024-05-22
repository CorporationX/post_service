package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    //TODO: думаю пока так будет, дальше посмотрим
    public void validateAuthor(PostDto postDto) {
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("The post does not have an author specified");
        }
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("A post cannot have two authors");
        }
        if (postDto.getAuthorId() != null && userServiceClient.getUser(postDto.getAuthorId()) == null) {
            throw new DataValidationException("The author must be an existing user in the system");
        }
        if (postDto.getProjectId() != null && projectServiceClient.getProject(postDto.getProjectId()) == null) {
            throw new DataValidationException("The author must be an existing project in the system");
        }
    }

    public void validatePostByUser(Post post, long userId) {
        if (post.getAuthorId() != userId) {
            throw new DataValidationException("You are not the author of this post");
        }
    }

    public void validatePostByProject(Post post, long projectId) {
        if (post.getProjectId() != projectId) {
            throw new DataValidationException("You are not the author of this post");
        }
    }

    public void isPublishedPost(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("The post cannot publish that has already been published before");
        }
    }

    public void isDeletedPost(Post post) {
        if (post.isDeleted()) {
            throw new DataValidationException("The post cannot delete that has already been deleted before");
        }
    }
}
