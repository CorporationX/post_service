package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    @Transactional
    public void createPostDraft(PostDto postDto) {
        postValidator.validatePostContent(postDto);
        postValidator.validatePost(postDto);
        postRepository.save(postMapper.toEntity(postDto));
    }

    @Transactional
    public void publishPost(long postId) {
        Post post = getPost(postId);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);

    }

    @Transactional
    public void updatePost(long postId, PostDto postDto) {
        Post post = getPost(postId);
        postValidator.validatePostContent(postDto);
        post.setContent(postDto.getContent());
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(long postId) {
        Post post = getPost(postId);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public PostDto getPostById(long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        Post post = postOpt.orElseThrow(() -> new DataValidationException("Post not found"));

        return postMapper.toDto(post);
    }

    public Post getPost(long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        return postOpt.orElseThrow(() -> new DataValidationException("Post not found"));
    }

    @Transactional
    public List<PostDto> getAuthorDrafts(long authorId) {
        postValidator.validateAuthor(authorId);
        return sortDrafts(authorId);
    }

    @Transactional
    public List<PostDto> getProjectDrafts(long projectId) {
        postValidator.validateProject(projectId);
        return sortDrafts(projectId);
    }

    @Transactional
    public List<PostDto> getAuthorPosts(long authorId) {
        postValidator.validateAuthor(authorId);
        return sortPosts(authorId);
    }

    @Transactional
    public List<PostDto> getProjectPosts(long projectId) {
        postValidator.validateProject(projectId);
        return sortPosts(projectId);
    }

    @Transactional
    public List<PostDto> sortDrafts(long ownerId) {
        List<Post> sortedPosts = postRepository.findByProjectId(ownerId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed()).toList();
        return postMapper.toDtoList(sortedPosts);
    }

    @Transactional
    public List<PostDto> sortPosts(long ownerId) {
        List<Post> sortedPosts = postRepository.findByProjectId(ownerId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed()).toList();
        return postMapper.toDtoList(sortedPosts);
    }
}
