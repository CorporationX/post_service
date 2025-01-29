package faang.school.postservice.service;

import faang.school.postservice.dto.posts.PostCreatingRequest;
import faang.school.postservice.dto.posts.PostResultResponse;
import faang.school.postservice.dto.posts.PostUpdatingDto;
import faang.school.postservice.exceptions.PostWasNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.utils.PostUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostUtil postUtil;

    @Transactional
    public PostResultResponse createPost(PostCreatingRequest postCreatingDto) {
        Post post = Post.builder()
                .content(postCreatingDto.content())
                .published(false)
                .deleted(false)
                .build();

        log.info("Creating the post with id : {}", post.getId());

        log.info("Validating the post creator with id : {}", post.getId());
        int result = postUtil.validateCreator(postCreatingDto.authorId(), postCreatingDto.projectId());
        switch (result) {
            case 0:
                post.setAuthorId(postCreatingDto.authorId());
                break;
            case 1:
                post.setProjectId(postCreatingDto.projectId());
                break;
            default:
        }
        log.info("Success validation for post : {}", post.getId());

        post = postRepository.save(post);
        log.info("Saved post with id : {}", post.getId());

        return postMapper.toDto(post);
    }

    @Transactional
    public PostResultResponse publishPost(Long postId) {
        log.info("Publishing post with id : {}", postId);
        Post post = findPostById(postId);
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post already published!");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        log.info("Successfully published post with id : {}", postId);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostResultResponse updatePost(PostUpdatingDto postUpdatingDto) {
        Long postId = postUpdatingDto.postId();
        String updatingContent = postUpdatingDto.updatingContent();
        log.info("Updating post with id : {}", postId);
        Post post = findPostById(postId);
        if (post.isDeleted() || !post.isPublished()) {
            throw new IllegalArgumentException("Post deleted or not published yet!");
        }
        post.setContent(updatingContent);
        log.info("Successfully updated post with id : {}", postId);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostResultResponse softDelete(Long postId) {
        log.info("Soft deleting post with id : {}", postId);
        Post post = findPostById(postId);
        if (post.isDeleted()) {
            throw new IllegalArgumentException("Post already marked as deleted!");
        }
        post.setDeleted(true);
        log.info("Successfully soft deleted post with id : {}", postId);
        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getNoPublishedPostsByAuthor(Long authorId) {
        return getPostsByFilter(authorId, postRepository::findByAuthorId, post -> !post.isPublished());
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getNoPublishedPostsByProject(Long projectId) {
        return getPostsByFilter(projectId, postRepository::findByProjectId, post -> !post.isPublished());
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getPublishedPostsByAuthor(Long authorId) {
        return getPostsByFilter(authorId, postRepository::findByAuthorId, Post::isPublished);
    }

    @Transactional(readOnly = true)
    public List<PostResultResponse> getPublishedPostsByProject(Long projectId) {
        return getPostsByFilter(projectId, postRepository::findByProjectId, Post::isPublished);
    }

    public List<PostResultResponse> getPostsByFilter(Long id,
                                                     Function<Long, List<Post>> fetcher,
                                                     Predicate<Post> filter) {
        return fetcher.apply(id)
                .stream()
                .filter(filter)
                .map(postMapper::toDto)
                .toList();
    }

    public Post findPostById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostWasNotFoundException("No posts was found!"));
    }
}
