package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostValidator postValidator;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;

    public PostDto createDraftPost(PostDto postDto) {
        UserDto author = null;
        ProjectDto project = null;

        if (postDto.getAuthorId() != null) {
            author = userServiceClient.getUser(postDto.getAuthorId());
        } else if (postDto.getProjectId() != null) {
            project = projectServiceClient.getProject(postDto.getProjectId());
        } else {
            throw new IllegalArgumentException("Необходимо указать автора или проект");
        }
        postValidator.validateAuthorExists(author, project);

        return savePost(postDto);
    }

    public PostDto savePost(PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        return postMapper.toDto(postRepository.save(post));
    }
}
