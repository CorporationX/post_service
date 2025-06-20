package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.post.PostValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.util.ValidationUtils.setIfNotNull;
import static faang.school.postservice.util.ValidationUtils.executeIfNotNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;

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

    @Transactional(readOnly = true)
    public List<Post> getAllUnpublishedPost() {
        return postRepository.findAllUnpublishedPosts();
    }

    public void updateCorrectedContentOfPost(Post post, String correctedContent){
        post.setContent(correctedContent);
        postRepository.save(post);
        log.info("post text ID: {} updated", post.getId());
    }

    private Post getValidPostOrThrowException(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        PostValidation.validatePostExists(postOptional.isPresent());
        return postOptional.get();
    }
}
