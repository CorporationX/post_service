package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final Clock clock;

    @Transactional
    @Override
    public PostDto createDraft(PostDto postDto) {
       // log.info("Creating draft post: {}", postDto);
        postValidator.validatePostDto(postDto);
        Post post = postMapper.toEntity(postDto);
        post.setCreatedAt(LocalDateTime.now(clock));
        Post savedPost = postRepository.save(post);
        log.info("Post saved with ID: {}", savedPost.getId());
        return postMapper.toDto(savedPost);
    }

    @Transactional
    @Override
    public PostDto getPost(long postId) {
        Post post = getPostById(postId);
        if(post.isDeleted()) {
            throw new PostNotFoundException("Post with ID = %d was deleted".formatted(postId));
        }
        return postMapper.toDto(post);
    }

    @Transactional
    @Override
    public PostDto publishPost(long postId) {
        Post post = getPostById(postId);
        if (post.isPublished()) {
            log.error("Post with ID {} is already published", postId);
            throw new PostValidationException("Post is already published");
        }
        post.setPublished(true);
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    @Override
    public PostDto updatePost(long postId, String content) {
        log.info("Updating post with ID {}", postId);
        Post post = getPostById(postId);
        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now(clock));
        Post updatedPost = postRepository.save(post);
        log.info("Post with ID: {} is updated", updatedPost.getId());
        return postMapper.toDto(post);
    }

    @Transactional
    @Override
    public void softDeletePost(long postId) {
        Post post = getPostById(postId);
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Transactional
    @Override
    public List<PostDto> getAllDraftsByAuthorId(long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        return filterPostsAndMapToDto(posts, post -> !post.isPublished());
    }

    @Transactional
    @Override
    public List<PostDto> getAllDraftsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return filterPostsAndMapToDto(posts, post -> !post.isPublished());
    }

    @Transactional
    @Override
    public List<PostDto> getAllPostsByAuthorId(long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        return filterPostsAndMapToDto(posts, Post::isPublished);
    }

    @Transactional
    @Override
    public List<PostDto> getAllPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return filterPostsAndMapToDto(posts, Post::isPublished);
    }

    private List<PostDto> filterPostsAndMapToDto(List<Post> posts, Predicate<Post> isPublishedCondition) {
        List<Post> filteredPosts = posts.stream()
                .filter(post -> !post.isDeleted())
                .filter(isPublishedCondition)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
        return postMapper.toDtoList(filteredPosts);
    }

    private Post getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Post with id %d does not exist".formatted(postId)));
    }

    @Override
    public Post findPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException(String.format("Post with id %s not found", postId)));

    }

}
