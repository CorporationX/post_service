package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.post.PostValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.util.ValidationUtils.setIfNotNull;
import static faang.school.postservice.util.ValidationUtils.executeIfNotNull;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Transactional
    public Post createPost(Post post) {
        PostValidation.validateNotNullAuthor(post);
        PostValidation.validateNotNullContent(post);
        userServiceClient.getUser(post.getAuthorId());
        executeIfNotNull(post.getProjectId(), () -> projectServiceClient.getProject(post.getProjectId()));
        postRepository.save(post);
        return getValidPostOrThrowException(post.getId());
    }

    @Transactional
    public boolean publishPost(Long postId) {
        Post post = getValidPostOrThrowException(postId);
        PostValidation.validateNotAlreadyPublishedPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);

        return getValidPostOrThrowException(postId).isPublished();
    }

    @Transactional
    public Post updatePost(Long postId, Post updateFields) {
        Post post = getValidPostOrThrowException(postId);
        post.setUpdatedAt(LocalDateTime.now());
        post.setContent(updateFields.getContent());
        setIfNotNull(updateFields.getScheduledAt(), post::setScheduledAt);
        postRepository.save(post);
        return getValidPostOrThrowException(postId);
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
    public List<Post> getNotDeletedDraftsByUserId(Long userId) {
        return postRepository.findNonDeletedDraftsByAuthorId(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> getNotDeletedDraftsByProjectId(Long projectId) {
        return postRepository.findNonDeletedDraftsByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<Post> getNotDeletedPublishedByUserId(Long userId) {
        return postRepository.findNonDeletedPublishedByAuthorId(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> getNotDeletedPublishedByProjectId(Long projectId) {
        return postRepository.findNonDeletedPublishedByProjectId(projectId);
    }

    private Post getValidPostOrThrowException(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        PostValidation.validatePostDoesNotExist(postOptional.isPresent());
        return postOptional.get();
    }
}
