package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator implements PostServiceValidator<PostDto> {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Override
    public void validate(PostDto postDto) {
        Long authorId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        if (authorId == null && projectId == null || authorId != null && projectId != null) {
            throw new IllegalArgumentException("Post must have either author or project");
        }

        if (authorId != null) {
            UserDto user = userServiceClient.getUser(authorId);

            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
        }

        if (projectId != null) {
            ProjectDto project = projectServiceClient.getProject(projectId);

            if (project == null) {
                throw new IllegalArgumentException("Project not found");
            }
        }
    }
}
