package faang.school.postservice.service;

import faang.school.postservice.config.context.ProjectContext;
import faang.school.postservice.config.context.UserContext;
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

    private final UserContext userContext;
    private final ProjectContext projectContext;
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
        postValidator.isPublishedPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(Long postId, PostDto postDto) {
        Post post = existsPost(postId);
        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto deletePost(long postId) {
        Post post = existsPost(postId);
        postValidator.isDeletedPost(post);
        post.setPublished(false);
        post.setDeleted(true);
        return postMapper.toDto(post);
    }

    public PostDto getPostById(long postId) {
        return postMapper.toDto(existsPost(postId));
    }

    public List<PostDto> getDraftsByAuthorId(long id) {
        return
    }

    public List<PostDto> getDraftsByProjectId(long id) {
        return
    }

    public List<PostDto> getPostsByAuthorId(long id) {
        return
    }

    public List<PostDto> getPostsByProjectId(long id) {
        return
    }

    protected Post existsPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post with ID " + postId + " not found"));
    }

    //TODO: Переписать валидаторы с использованием данных методов
    private Long getUserId() {
        return userContext.getUserId();
    }
    private Long getProjectId() {
        return projectContext.getProjectId();
    }
}