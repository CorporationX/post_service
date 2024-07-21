package faang.school.postservice.service;


import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    private PostDto createPostToProject(PostDto postDto) {
        ProjectDto project = projectServiceClient.getProject(postDto.getProjectId());
        if (project == null) {
            throw new DataValidationException("Project not found");
        }
        Post createdDraft = postRepository.save(postMapper.toEntity(postDto));
        return postMapper.toDto(createdDraft);
    }

    private PostDto createPostToAuthor(PostDto postDto) {
        UserDto user = userServiceClient.getUser(postDto.getAuthorId());
        if (user == null) {
            throw new DataValidationException("User not found");
        }
        Post createdDraft = postRepository.save(postMapper.toEntity(postDto));
        return postMapper.toDto(createdDraft);
    }

    public PostDto createDraft(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            return createPostToAuthor(postDto);
        } else {
            return createPostToProject(postDto);
        }
    }

    public PostDto publishDraft(Long postId) {
        Optional<Post> postReadyToPublish = postRepository.findReadyToPublish()
                .stream().filter(el -> el.getId() == postId).findFirst();
        if (postReadyToPublish.isEmpty()) {
            throw new EntityNotFoundException("Post not found");
        }

        Post post = postReadyToPublish.get();
        if (post.getPublishedAt() != null) {
            throw new DataValidationException("Post is already published");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }
}
