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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        postValidator.validationOfPostCreation(post);

        Post postEntity = postMapper.toPost(post);

        return postMapper.toDto(postRepository.save(postEntity));
    }

    @Transactional
    public PostDto publishPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        postValidator.validatePublishPost(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public List<PostDto> getNotDeletedDraftsByAuthorId(Long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        postValidator.validateAuthor(user);
        List<Post> postByAuthorId = postRepository.findByAuthorId(user.getId());
        return postByAuthorId.stream().filter(post -> !post.isDeleted() && !post.isPublished()).map(postMapper::toDto).toList();
    }

    @Transactional
    public List<PostDto> getNotDeletedDraftsByProjectId(Long projectId) {
        ProjectDto project = projectServiceClient.getProject(projectId);
        postValidator.validateProject(project);
        List<Post> postByProjectId = postRepository.findByProjectId(project.getId());
        return postByProjectId.stream().filter(post -> !post.isDeleted() && !post.isPublished()).map(postMapper::toDto).toList();
    }

    @Transactional
    public List<PostDto> getNotDeletedPublishedPostsByAuthorId(Long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        postValidator.validateAuthor(user);
        List<Post> postByAuthorId = postRepository.findByAuthorId(user.getId());
        return postByAuthorId.stream().filter(post -> !post.isDeleted() && post.isPublished()).map(postMapper::toDto).toList();
    }

    @Transactional
    public List<PostDto> getNotDeletedPublishedPostsByProjectId(Long projectId) {
        ProjectDto project = projectServiceClient.getProject(projectId);
        postValidator.validateProject(project);
        List<Post> postByProjectId = postRepository.findByProjectId(project.getId());
        return postByProjectId.stream().filter(post -> !post.isDeleted() && post.isPublished()).map(postMapper::toDto).toList();
    }
}
