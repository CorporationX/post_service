package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.validation.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final UserContext userContext;

    @Transactional(readOnly = true)
    public Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post with id {} not found", postId);
                    return new PostNotFoundException(postId);
                });
    }

    @Transactional
    public Post createDraftPost(final Post post) {
        postValidator.checkPost(post);

        if (post.getProjectId() == null) {
            long userId = userContext.getUserId();
            post.setAuthorId(userId);
        }

        Post savedPost = postRepository.save(post);
        log.info("Post with id {} has been created", savedPost.getId());
        return savedPost;
    }

    @Transactional
    public Post publishPost(long postId) {
        Post post = getPostById(postId);

        postValidator.checkPostIsNotPublished(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post = postRepository.save(post);
        log.info("Post with id {} has been published on {}", post.getId(), post.getPublishedAt());

        return post;
    }

    @Transactional
    public Post updatePost(final Post post) {
        Post savedPost = postRepository.save(post);
        log.info("Post with id {} has been update", savedPost.getId());
        return savedPost;
    }

    @Transactional
    public void deletePost(long postId) {
        Post post = getPostById(postId);

        post.setDeleted(true);
        post.setDeletedAt(LocalDateTime.now());
        post = postRepository.save(post);
        log.info("Post with id {} has been delete on {}", post.getId(), post.getDeletedAt());
    }

    @Transactional(readOnly = true)
    public List<Post> getAllDraftPostsByUserId() {
        long userId = userContext.getUserId();
        return postRepository.findByAuthorIdAndDeletedFalseAndPublishedFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> getAllDraftPostsByProjectId(long projectId) {
        return postRepository.findByProjectIdAndDeletedFalseAndPublishedFalseOrderByCreatedAtDesc(projectId);
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPublishedPostsByUserId() {
        long userId = userContext.getUserId();
        return postRepository.findByAuthorIdAndDeletedFalseAndPublishedTrueOrderByPublishedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPublishedPostsByProjectId(long projectId) {
        return postRepository.findByProjectIdAndDeletedFalseAndPublishedTrueOrderByPublishedAtDesc(projectId);
    }
}
