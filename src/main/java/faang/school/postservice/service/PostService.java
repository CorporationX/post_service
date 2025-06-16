package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
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

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no such id = " + id));
    }

    public PostDto getPostDtoById(long id) {
        return postMapper.toDto(postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no such id = " + id)));
    }

    public PostDto createDraftPost(PostDto dto) {
        Post post = postMapper.toEntity(dto);
        post.setCreatedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto publishPost(long postId) {
        Post post = getPostById(postId);
        if (post.isPublished()) {
            throw new IllegalStateException("Post already posted.");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto updatePost(long postId, PostDto dto) {
        Post post = getPostById(postId);
        post.setContent(dto.content());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto deletePost(long postId) {
        Post post = getPostById(postId);
        post.setDeleted(true);
        return postMapper.toDto(postRepository.save(post));
    }

    public List<PostDto> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::toDto)
                .toList();
    }

    public void deletePostById(Long postId) {
        postRepository.deleteById(postId);
    }

    public List<PostDto> getAllNotDeletedDraftsByAuthorId(Long authorId) {
        return getDraftsById(postRepository.findByAuthorId(authorId));
    }

    public List<PostDto> getAllNotDeletedDraftsByProjectId(Long projectId) {
        return getDraftsById(postRepository.findByProjectId(projectId));
    }

    public List<PostDto> getAllPostsByAuthorId(Long authorId) {
        return getPostsById(postRepository.findByAuthorId(authorId));
    }

    public List<PostDto> getAllPostsByProjectId(Long projectId) {
        return getPostsById(postRepository.findByProjectId(projectId));
    }

    private List<PostDto> getDraftsById(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::createdAt))
                .toList();
    }

    private List<PostDto> getPostsById(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::publishedAt))
                .toList();
    }
}
