package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepo;
    private final UserContext userContext;

    public void validatePost(PostDto postDto) {
        validatePostExists(postDto.getId());
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("Post cannot belong to both author and project");
        }
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("Post must belong to either author or project");
        }
    }

    public void validatePostByOwner(Post post) {
        long userId = userContext.getUserId();

        if (post.getAuthorId() != null) {
            if (post.getAuthorId() != userId) {
                throw new DataValidationException("You are not the author of the post");
            }
        } else {
            List<Long> projectIdsUserHasAccess = projectServiceClient.getAll()
                    .stream().filter(prj -> prj.getOwnerId() == userId)
                    .map(ProjectDto::getId)
                    .toList();
            if (!projectIdsUserHasAccess.contains(post.getProjectId())) {
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

    public void validatePostExists(long id) {
        if (postRepo.existsById(id)) {
            throw new DataValidationException("Post with id: " + id + " already exists");
        }
    }
}