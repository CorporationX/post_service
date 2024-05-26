package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final PostRepository postRepository;

    @Transactional
    public PostDto createPost(PostDto postDto) {
        postValidator.validateAuthor(postDto);
        Post post = postMapper.toEntity(postDto);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publishPost(long postId) {
        Post post = existsPost(postId);
        postValidator.checkPostAuthorship(post);
        postValidator.isPublishedPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(Long postId, PostDto postDto) {
        Post post = existsPost(postId);
        postValidator.checkPostAuthorship(post);
        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto deletePost(long postId) {
        Post post = existsPost(postId);
        postValidator.checkPostAuthorship(post);
        postValidator.isDeletedPost(post);
        post.setPublished(false);
        post.setDeleted(true);
        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public PostDto getPostById(long postId) {
        return postMapper.toDto(existsPost(postId));
    }

    @Transactional(readOnly = true)
    public List<PostDto> getDraftsByAuthorId(long id) {
        postValidator.validateUserExist(id);
        List<Post> posts = postRepository.findDraftPostsByAuthor(id);
        return postMapper.toDto(posts);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getDraftsByProjectId(long id) {
        postValidator.validateProjectExist(id);
        List<Post> posts = postRepository.findDraftPostsByProject(id);
        return postMapper.toDto(posts);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPostsByAuthorId(long id) {
        postValidator.validateUserExist(id);
        List<Post> posts = postRepository.findPublishedPostsByAuthor(id);
        return postMapper.toDto(posts);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPostsByProjectId(long id) {
        postValidator.validateProjectExist(id);
        List<Post> posts = postRepository.findPublishedPostsByProject(id);
        return postMapper.toDto(posts);
    }

    protected Post existsPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post with ID " + postId + " not found"));
    }
}