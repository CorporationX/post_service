package faang.school.postservice.service;


import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Transactional
    public PostDto createDraft(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            return createPostToAuthor(postDto);
        } else {
            return createPostToProject(postDto);
        }
    }

    @Transactional
    public PostDto publishDraft(Long postId) {
        List<Post> readyToPublish = postRepository.findReadyToPublish();
        Optional<Post> postReadyToPublish = readyToPublish
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
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(Long id, UpdatePostDto updatePostDto) {
        Post postToUpdate = getPost(id);
        postToUpdate.setContent(updatePostDto.getContent());
        return postMapper.toDto(postToUpdate);
    }

    @Transactional
    public void delete(Long id) {
        Post postToDelete = getPost(id);
        postToDelete.setDeleted(true);
    }

    public PostDto getPostById(Long id) {
        Post post = getPost(id);
        return postMapper.toDto(post);
    }

    public List<PostDto> getDraftsByUser(Long id) {
        UserDto user = userServiceClient.getUser(id);
        List<Post> postsByAuthor = getFilteredPostsByUser(user.getId(), (post) -> !post.isPublished());
        return postMapper.toDtoList(postsByAuthor);
    }

    public List<PostDto> getDraftsByProject(Long id) {
        ProjectDto projectDto = projectServiceClient.getProject(id);
        List<Post> postsByProject = getFilteredPostsByProject(projectDto.getId(), (post) -> !post.isPublished());
        return postsByProject.stream().map(postMapper::toDto).toList();
    }


    public List<PostDto> getPublishedByUser(Long id) {
        UserDto user = userServiceClient.getUser(id);
        List<Post> publishedPostsByAuthor = getFilteredPostsByUser(user.getId(), Post::isPublished);
        return publishedPostsByAuthor.stream().map(postMapper::toDto).toList();
    }

    public List<PostDto> getPublishedByProject(Long id) {
        ProjectDto projectDto = projectServiceClient.getProject(id);
        List<Post> publishedPostsByProject = getFilteredPostsByProject(projectDto.getId(), Post::isPublished);
        return publishedPostsByProject.stream().map(postMapper::toDto).toList();
    }

    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    private List<Post> getFilteredPostsByUser(Long userId, Predicate<Post> filter) {
        return postRepository.findByAuthorId(userId)
                .stream().filter(filter).toList();
    }

    private List<Post> getFilteredPostsByProject(Long projectId, Predicate<Post> filter) {
        return postRepository.findByProjectId(projectId)
                .stream().filter(filter).toList();
    }

    private PostDto createPostToProject(PostDto postDto) {
        projectServiceClient.getProject(postDto.getProjectId());
        return getPostDto(postDto);
    }

    private PostDto createPostToAuthor(PostDto postDto) {
        userServiceClient.getUser(postDto.getAuthorId());
        return getPostDto(postDto);
    }

    private PostDto getPostDto(PostDto postDto) {
        Post createdDraft = postRepository.save(postMapper.toEntity(postDto));
        return postMapper.toDto(createdDraft);
    }
}