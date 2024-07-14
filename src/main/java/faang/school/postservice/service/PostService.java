package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostServiceValidator postServiceValidator;

    public void createPost(PostDto postDto) {
        postServiceValidator.validateCreatePost(postDto);
        postDto.setCreatedAt(LocalDateTime.now());
        postDto.setUpdatedAt(LocalDateTime.now());

        postRepository.save(postMapper.toEntity(postDto));
    }

    public void updatePost(PostDto postDto) {
        postServiceValidator.validateUpdatePost(postDto);
        postDto.setUpdatedAt(LocalDateTime.now());

        postRepository.save(postMapper.toEntity(postDto));
    }

    public void publishPost(PostDto postDto) {
        postServiceValidator.validatePublishPost(postDto);
        postDto.setPublished(true);
        postDto.setPublishedAt(LocalDateTime.now());
        postDto.setUpdatedAt(LocalDateTime.now());

        postRepository.save(postMapper.toEntity(postDto));
    }

    public void deletePost(Long postId) {
        Post postFromTheDatabase = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post not found"));
        postServiceValidator.validateDeletePost(postFromTheDatabase);

        if (!postFromTheDatabase.isDeleted()) {
            postFromTheDatabase.setDeleted(true);
        }
        if (postFromTheDatabase.isPublished()) {
            postFromTheDatabase.setPublished(false);
        }
        postFromTheDatabase.setUpdatedAt(LocalDateTime.now());

        postRepository.save(postFromTheDatabase);
    }

    public PostDto getPostByPostId(Long postId) {
        return postMapper.toDto(postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post not found")));
    }


    public List<PostDto> getAllDraftPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<Post> filteredPosts = posts.stream()
                .filter(post -> !post.isPublished())
                .toList();

        return postMapper.toDto(sortPostsByCreateAt(filteredPosts));
    }

    public List<PostDto> getAllDraftPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<Post> filteredPosts = posts.stream()
                .filter(post -> !post.isPublished())
                .toList();

        return postMapper.toDto(sortPostsByCreateAt(filteredPosts));
    }

    public List<PostDto> getAllPublishPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<Post> filteredPosts = posts.stream()
                .filter(Post::isPublished)
                .toList();

        return postMapper.toDto(sortPostsByPublishAt(filteredPosts));
    }

    public List<PostDto> getAllPublishPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<Post> filteredPosts = posts.stream()
                .filter(Post::isPublished)
                .toList();

        return postMapper.toDto(sortPostsByPublishAt(filteredPosts));
    }

    private List<Post> sortPostsByCreateAt(List<Post> posts) {
        return posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    private List<Post> sortPostsByPublishAt(List<Post> posts) {
        return posts.stream()
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }
}
