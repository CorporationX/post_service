package faang.school.postservice.service.post;

import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.exception.post.PostPublishedException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final PostHashTagService postHashTagService;
    private final PostCacheService postCacheService;
    private final PostMapper postMapper;

    @Transactional
    public Post create(Post post) {
        log.info("Create post with id: {}", post.getId());
        postValidator.validateCreatePost(post);

        post.setPublished(false);
        post.setDeleted(false);
        post.setCreatedAt(LocalDateTime.now());
        postHashTagService.updateHashTags(post);

        return postRepository.save(post);
    }

    @Transactional
    public Post update(Post updatePost) {
        log.info("Update post with id: {}", updatePost.getId());
        Post post = findPostById(updatePost.getId());
        List<String> primaTags = new ArrayList<>(post.getHashTags());

        post.setContent(updatePost.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        postHashTagService.updateHashTags(post);

        if (!post.isDeleted() && post.isPublished()) {
            postCacheService.updatePostProcess(postMapper.toPostCacheDto(post), primaTags);
        }
        return postRepository.save(post);
    }

    @Transactional
    public Post publish(Long id) {
        log.info("Publish post with id: {}", id);
        Post post = findPostById(id);
        if (post.isPublished()) {
            throw new PostPublishedException(id);
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postHashTagService.updateHashTags(post);
        postCacheService.newPostProcess(postMapper.toPostCacheDto(post));

        return postRepository.save(post);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Delete post with id: {}", id);
        Post post = findPostById(id);

        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());
        postCacheService.deletePostProcess(postMapper.toPostCacheDto(post), post.getHashTags());

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Post findPostById(Long id) {
        log.info("Find post with id: {}", id);
        return postRepository.findByIdAndNotDeleted(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Post> searchByAuthor(Post filterPost) {
        List<Post> posts = postRepository.findByAuthorId(filterPost.getAuthorId());
        posts = applyFiltersAndSorted(posts, filterPost)
                .toList();

        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> searchByProject(Post filterPost) {
        List<Post> posts = postRepository.findByProjectId(filterPost.getProjectId());
        posts = applyFiltersAndSorted(posts, filterPost)
                .toList();

        return posts;
    }

    private Stream<Post> applyFiltersAndSorted(List<Post> posts, Post filterPost) {
        return posts.stream()
                .filter((post -> !post.isDeleted()))
                .filter((post -> post.isPublished() == filterPost.isPublished()))
                .sorted(Comparator.comparing(
                        filterPost.isPublished() ? Post::getPublishedAt : Post::getCreatedAt
                ).reversed());
    }
}
