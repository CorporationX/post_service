package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepo;

    public void validatePost(PostDto postDto) {
        validatePostExists(postDto.getId());
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("Post cannot belong to both author and project");
        }
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("Post must belong to either author or project");
        }
    }

    public void validatePostByOwner(long postId, long ownerId) {
        Post post = postRepo.findById(postId).get();

        if (post.getAuthorId() != null) {
            if (post.getAuthorId() != ownerId) {
                throw new DataValidationException("You are not the author of the post");
            }
        } else {
            if (post.getProjectId() != ownerId) {
                throw new DataValidationException("Project is not the author of the post");
            }
        }
    }

    public void validatePostOwnerExists(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            userServiceClient.existsUserById(postDto.getAuthorId());
        } else {
            projectServiceClient.existsProjectById(postDto.getProjectId());
        }

    }

    public void validateAuthor(long authorId) {
        userServiceClient.existsUserById(authorId);
    }

    public void validateProject(long projectId) {
        projectServiceClient.existsProjectById(projectId);
    }

    public void validatePostExists(long id) {
        if (postRepo.existsById(id)) {
            throw new DataValidationException("Post with id: " + id + " already exists");
        }
    }
}
