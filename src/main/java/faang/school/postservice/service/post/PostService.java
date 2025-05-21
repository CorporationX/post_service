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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    // TODO: проверка что пользователь или проект существуют
    @Transactional
    public Post createDraftPost(final Post post) {
        postValidator.validatePost();
        Post savedPost = postRepository.save(post);
        log.info("Post with id {} has been created", savedPost.getId());
        return savedPost;
    }

    // TODO: можно сделать отложенную публикацию
    @Transactional
    public Post publishPost(final long postId) {
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

    // TODO: возможно кастомный запрос
    @Transactional(readOnly = true)
    public List<Post> getAllDraftPostsByUserId() {
        long userId = userContext.getUserId();
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> Objects.equals(post.isDeleted(), false))
                .filter(post -> Objects.equals(post.isPublished(), false))
                .sorted(Comparator.comparingInt(post -> post.getCreatedAt().getSecond()))
                .toList();
    }
}
