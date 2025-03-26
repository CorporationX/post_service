package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST_NOT_EXIST = "Post doesn't exist";

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public PostDto createDraft(PostDto postDto) {
//        validatePostDto(postDto); TODO: remove the // when UserController and ProjectController are ready
        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostDto publishPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_EXIST));
        if (post.isPublished()) {
            throw new IllegalStateException("Post is already published");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostDto updatePost(Long postId, PostDto postDto) {
//        validatePostDto(postDto); TODO: remove the // when UserController and ProjectController are ready
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_EXIST));
        if (postDto.getAuthorId() != null && !postDto.getAuthorId().equals(post.getAuthorId())) {
            throw new IllegalArgumentException("Cannot change the author of the post");
        }
        if (postDto.getProjectId() != null && !postDto.getProjectId().equals(post.getProjectId())) {
            throw new IllegalArgumentException("Cannot change the project of the post");
        }
        postMapper.update(postDto, post);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostDto softDelete(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_EXIST));
        post.setDeleted(true);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostDto getPostById(Long postId) {
        return postMapper.toDto(postRepository
                .findById(postId).orElseThrow(() -> new NotFoundException("The post hasn't been found")));
    }

    public List<PostDto> getAllDraftsByAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> getAllDraftsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> getAllPublishedPostsByAuthorId(Long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        return posts.stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto).toList();
    }

    public List<PostDto> getAllPublishedPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private void validatePostDto(PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new IllegalArgumentException("Post can have only one author: either user or project");
        }
        if (postDto.getAuthorId() != null) {
            UserDto userDto = userServiceClient.getUser(postDto.getAuthorId());
            if (userDto == null) {
                throw new NotFoundException("Author doesn't exist");
            }
        }
        if (postDto.getProjectId() != null) {
            ProjectDto projectDto = projectServiceClient.getProject(postDto.getProjectId());
            if (projectDto == null) {
                throw new NotFoundException("Project doesn't exist");
            }
        }
    }
    
    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with id: " + id + " not found"));
    }

    public void removeTagsFromPost(Long postId, List<Long> tagsId) {
        postRepository.deleteTagsFromPost(postId, tagsId);
    }
}
