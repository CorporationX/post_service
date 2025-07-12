package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.post.PostValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.util.ValidationUtils.executeIfNotNull;
import static faang.school.postservice.util.ValidationUtils.setIfNotNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    @Value("${scheduled-post-publisher.comments.batch_size}")
    private int batchSize;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;
    private final PostBatchPublisher postBatchPublisher;
    private final PostCorrecter postCorrecter;

    @Transactional
    public Post createPost(Post post) {
        long userId = userContext.getUserId();
        post.setAuthorId(userId);
        PostValidation.validateNotNullAuthor(post);
        PostValidation.validateNotNullContent(post);
        userServiceClient.getUser(userId);
        executeIfNotNull(post.getProjectId(), () -> projectServiceClient.getProject(post.getProjectId()));
        return postRepository.save(post);
    }

    @Transactional
    public boolean publishPost(Long postId) {
        Post post = getValidPostOrThrowException(postId);
        PostValidation.validateNotAlreadyPublishedPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        return post.isPublished();
    }

    @Transactional
    public Post updatePost(Long postId, Post updateFields) {
        Post post = getValidPostOrThrowException(postId);
        post.setUpdatedAt(LocalDateTime.now());
        setIfNotNull(updateFields.getContent(), post::setContent);
        setIfNotNull(updateFields.getScheduledAt(), post::setScheduledAt);
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = getValidPostOrThrowException(postId);
        PostValidation.validateNotAlreadyDeletedPost(post);

        post.setDeleted(true);
        postRepository.save(post);
    }

    @Transactional
    public void publishScheduledPosts() {
        log.info("Publishing scheduled posts...");
        List<Post> readyToPublish = postRepository.findReadyToPublish();

        if (readyToPublish.isEmpty()) {
            log.info("No scheduled posts found");
            return;
        }

        List<List<Post>> batches = ListUtils.partition(readyToPublish, batchSize);

        for (List<Post> batch : batches) {
            postBatchPublisher.publishPosts(batch);
        }
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return getValidPostOrThrowException(postId);
    }

    @Transactional(readOnly = true)
    public List<Post> getAllDraftsByAuthorId(Long authorId) {
        return postRepository.findDraftsByAuthorId(authorId);
    }

    @Transactional(readOnly = true)
    public List<Post> getAllDraftsByProjectId(Long projectId) {
        return postRepository.findDraftsByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPublishedByAuthorId(Long authorId) {
        return postRepository.findPublishedByAuthorId(authorId);
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPublishedByProjectId(Long projectId) {
        return postRepository.findPublishedByProjectId(projectId);
    }

    @Transactional
    @Async("executorForPostService")
    public void correctingContentBatchPostsAsync(int batch) {
        log.info("Correcting batch posts, begin transactional");
        List<Post> posts = postRepository.fetchDraftPostsBatchWithLock(batch);

        postCorrecter.correctingBatchPosts(posts);
        postRepository.saveAll(posts);
        log.info("Corrected batch posts, commit transactional");
    }

    private Post getValidPostOrThrowException(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        PostValidation.validatePostExists(postOptional.isPresent());
        return postOptional.get();
    }

//    @Transactional
//    public List<Post> getNotVerifiedPosts() {
//        return postRepository.getNotVerifiedPostsLimitedWithLock();
//    }
}
