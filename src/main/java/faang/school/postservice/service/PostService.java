package faang.school.postservice.service;

import faang.school.postservice.exception.PostAlreadyDeletedException;
import faang.school.postservice.exception.PostCreationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.UnauthorizedPostAccessException;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.producer.KafkaPostViewProducer;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final KafkaPostProducer kafkaPostProducer;
    private final KafkaPostViewProducer kafkaPostViewProducer;

    @Transactional
    public Post createPost(String content, Long authorId, Long projectId, boolean shouldPublish) {
        log.info("Creating new post for author ID: {}, project ID: {}", authorId, projectId);

        try {
            LocalDateTime now = LocalDateTime.now();

            Post post = Post.builder()
                    .content(content)
                    .authorId(authorId)
                    .projectId(projectId)
                    .published(shouldPublish)
                    .publishedAt(shouldPublish ? now : null)
                    .deleted(false)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            Post savedPost = postRepository.save(post);
            log.info("Successfully saved post with ID: {} for author: {}", savedPost.getId(), authorId);

            if (savedPost.isPublished()) {
                try {
                    kafkaPostProducer.sendPostCreatedEvent(savedPost);
                    log.debug("Post created event sent to Kafka for post ID: {}", savedPost.getId());
                } catch (Exception kafkaException) {
                    log.error("Failed to send post created event to Kafka for post ID: {}",
                            savedPost.getId(), kafkaException);
                }
            } else {
                log.debug("Post ID: {} is not published, skipping Kafka event", savedPost.getId());
            }

            return savedPost;

        } catch (Exception e) {
            log.error("Failed to create post for author ID: {}", authorId, e);
            throw new PostCreationException(authorId);
        }
    }

    @Transactional
    public Post createPost(String content, Long authorId) {
        return createPost(content, authorId, null, true);
    }

    @Transactional
    public Post publishPost(Long postId, Long authorId) {
        log.info("Publishing post ID: {} by author ID: {}", postId, authorId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getAuthorId().equals(authorId)) {
            throw new UnauthorizedPostAccessException(authorId, postId);
        }

        if (post.isDeleted()) {
            throw new PostAlreadyDeletedException(postId);
        }

        if (post.isPublished()) {
            log.info("Post ID: {} is already published", postId);
            return post;
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        Post publishedPost = postRepository.save(post);

        try {
            kafkaPostProducer.sendPostCreatedEvent(publishedPost);
            log.info("Post published and event sent to Kafka for post ID: {}", postId);
        } catch (Exception kafkaException) {
            log.error("Failed to send post published event to Kafka for post ID: {}",
                    postId, kafkaException);
        }

        return publishedPost;
    }

    @Transactional
    public Post schedulePost(String content, Long authorId, Long projectId, LocalDateTime scheduledAt) {
        log.info("Scheduling post for author ID: {} at {}", authorId, scheduledAt);

        LocalDateTime now = LocalDateTime.now();

        Post post = Post.builder()
                .content(content)
                .authorId(authorId)
                .projectId(projectId)
                .published(false)
                .scheduledAt(scheduledAt)
                .deleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Post savedPost = postRepository.save(post);
        log.info("Successfully scheduled post with ID: {} for {}", savedPost.getId(), scheduledAt);

        return savedPost;
    }

    @Transactional
    public Post updatePost(Long postId, String newContent, Long authorId) {
        log.info("Updating post ID: {} by author ID: {}", postId, authorId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getAuthorId().equals(authorId)) {
            throw new UnauthorizedPostAccessException(authorId, postId);
        }

        if (post.isDeleted()) {
            throw new PostAlreadyDeletedException(postId);
        }

        post.setContent(newContent);
        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);
        log.info("Successfully updated post ID: {}", postId);

        return updatedPost;
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        log.debug("Fetching post by ID: {} (without view tracking)", postId);

        return postRepository.findById(postId)
                .filter(post -> !post.isDeleted())
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    @Transactional(readOnly = true)
    public Post viewPost(Long postId, Long viewerId, String ipAddress, String userAgent, String source, String sessionId) {
        log.debug("User {} is viewing post {}", viewerId, postId);

        Post post = postRepository.findById(postId)
                .filter(p -> !p.isDeleted())
                .filter(p -> p.isPublished())
                .orElseThrow(() -> new PostNotFoundException(postId));

        try {
            kafkaPostViewProducer.sendPostViewEvent(
                    postId,
                    viewerId,
                    post.getAuthorId(),
                    post.getProjectId(),
                    ipAddress,
                    userAgent,
                    source,
                    sessionId
            );
            log.debug("Post view event sent for post ID: {} by user ID: {}", postId, viewerId);
        } catch (Exception kafkaException) {
            log.error("Failed to send post view event for post ID: {}, viewer ID: {}",
                    postId, viewerId, kafkaException);
        }

        return post;
    }

    @Transactional(readOnly = true)
    public Post viewPost(Long postId, Long viewerId) {
        return viewPost(postId, viewerId, null, null, "api", null);
    }

    @Transactional
    public void deletePost(Long postId, Long authorId) {
        log.info("Deleting post ID: {} by author ID: {}", postId, authorId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getAuthorId().equals(authorId)) {
            throw new UnauthorizedPostAccessException(authorId, postId);
        }

        post.setDeleted(true);
        post.setPublished(false);
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
        log.info("Successfully deleted post ID: {}", postId);
    }
}
