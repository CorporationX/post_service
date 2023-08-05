package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Transactional
    public PostDto createPost(PostDto post) {
        ProjectDto project = null;
        UserDto user = null;

        if (post.getProjectId() != null) {
            project = projectServiceClient.getProject(post.getProjectId());
        } else if (post.getAuthorId() != null) {
            user = userServiceClient.getUser(post.getAuthorId());
        }

        postValidator.validatePostCreator(post, project, user);
        postValidator.validatePostContent(post);

        Post postEntity = postMapper.toPost(post);

        return postMapper.toDto(postRepository.save(postEntity));
    }

    @Transactional
    public PostDto updatePost(PostDto postUpdateDto) {
        Post post = postRepository.findById(postUpdateDto.getId()).orElse(null);
        postValidator.validationOfPostUpdate(postUpdateDto, post);

        postMapper.update(postUpdateDto, post);

        return postMapper.toDto(postRepository.save(post));
    }
}
