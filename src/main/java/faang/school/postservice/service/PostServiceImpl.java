package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Transactional
@Slf4j
public class PostServiceImpl implements PostService {

    private static final String POST_NOT_EXIST = "Post doesn't exist";

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Override
    public PostDto createDraft(PostDto postDto) {
//        validatePostDto(postDto); TODO: remove the // when UserController and ProjectController are ready
        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
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

    @Override
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

    @Override
    public PostDto softDelete(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_EXIST));
        post.setDeleted(true);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    public PostDto getPostById(Long postId) {
        return postMapper.toDto(postRepository
                .findById(postId).orElseThrow(() -> new NotFoundException("The post hasn't been found")));
    }

    @Override
    public List<PostDto> getAllDraftsByAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getAllDraftsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getAllPublishedPostsByAuthorId(Long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        return posts.stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto).toList();
    }

    @Override
    public List<PostDto> getAllPublishedPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public Post getPostEntryById(@Min(1) long id) {
        log.debug("Fetching post with ID: {}", id);

        log.debug("Search for fasting in the database");
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            log.error("Post with ID {} not found", id);
            throw new EntityNotFoundException("Post not found");
        }

        log.debug("Post with ID {} fetched successfully", id);
        return postOptional.get();
    }

    @Override
    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with id: " + id + " not found"));
    }

    @Override
    public void removeTagsFromPost(Long postId, List<Long> tagsId) {
        postRepository.deleteTagsFromPost(postId, tagsId);
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
}
