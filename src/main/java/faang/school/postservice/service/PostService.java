package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
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
    private final PostValidator postValidator;

    public PostDto create(PostDto postDto) {
        postValidator.validateAuthorIdAndProjectId(postDto.getAuthorId(), postDto.getProjectId());
        postValidator.validatePostContent(postDto.getContent());
        Post post = postRepository.save(postMapper.toEntity(postDto));
        return postMapper.toDto(post);
    }

    public PostDto publish(Long postId) {
        postValidator.validateId(postId);
        Post post = findById(postId);
        postValidator.validatePublicationPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostDto update(Long postId, String content) {
        postValidator.validateId(postId);
        postValidator.validatePostContent(content);
        Post post = findById(postId);
        post.setContent(content);
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    public void deleteById(Long postId) {
        postValidator.validateId(postId);
        Post post = findById(postId);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post with this ID does not exist"));
    }

    public List<PostDto> getAllPostsDraftsByUserAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }

    public List<PostDto> getAllPostsDraftsByProjectAuthorId(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }

    public List<PostDto> getAllPublishedNonDeletedPostsByUserAuthorId(Long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }

    public List<PostDto> getAllPublishedNonDeletedPostsByProjectAuthorId(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }
}