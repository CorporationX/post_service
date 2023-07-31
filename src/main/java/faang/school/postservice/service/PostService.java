package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public PostDto createPost(PostDto post) {
        if (post.getAuthorId() != null && post.getProjectId() != null) {
            throw new DataValidationException("Author and project cannot be specified at the same time");
        }

        if (post.getAuthorId() != null) {
            validateAuthor(post);
        } else if (post.getProjectId() != null) {
            validateProject(post);
        }

        postValidator.validationOfPostCreation(post);

        Post postEntity = postMapper.toPost(post);

        return postMapper.toDto(postRepository.save(postEntity));
    }

    private void validateProject(PostDto post) {
        ProjectDto project = projectServiceClient.getProject(post.getProjectId());
        if (project == null) {
            throw new EntityNotFoundException("Project not found");
        }
    }

    private void validateAuthor(PostDto post) {
        UserDto user = userServiceClient.getUser(post.getAuthorId());
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
    }
}
