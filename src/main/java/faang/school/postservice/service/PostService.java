package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostValidator postValidator;

    public PostDto createPost(PostDto post) {
        UserDto user = userServiceClient.getUser(post.getAuthorId());
        ProjectDto project = projectServiceClient.getProject(post.getProjectId());

        postValidator.validateCreate(post, user, project);
        Post postEntity = postMapper.toPost(post);
        postRepository.save(postEntity);

        return post;
    }
}
