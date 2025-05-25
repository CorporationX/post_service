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
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Transactional
    public void createPost(Post post) {
        boolean isExist = postRepository.existsById(post.getId());
        PostValidation.validatePostExists(isExist);
        PostValidation.validateNotNullAuthor(post);
        PostValidation.validateNotNullContent(post);
        userServiceClient.getUser(post.getAuthorId());
        if (Objects.nonNull(post.getProjectId())) {
            projectServiceClient.getProject(post.getProjectId());
        }

        postRepository.save(post);
    }

    @Transactional
    public void publishPost(Long postId) {
        Post post = getValidPostOrThrowException(postId);
        PostValidation.validateNotAlreadyPublishedPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(Long postId, String content, LocalDateTime scheduledAt) {
        Post post = getValidPostOrThrowException(postId);
        post.setUpdatedAt(LocalDateTime.now());
        post.setContent(content);
        if (Objects.nonNull(scheduledAt)) {
            post.setScheduledAt(scheduledAt);
        }
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = getValidPostOrThrowException(postId);
        PostValidation.validateNotAlreadyDeletedPost(post);
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Transactional
    public Post getPostById(Long postId) {
        return getValidPostOrThrowException(postId);
    }

    @Transactional
    public List<Post> getNotDeletedDraftsByUserId(Long userId) {
        return postRepository.findNonDeletedDraftsByAuthorId(userId);
    }

    @Transactional
    public List<Post> getNotDeletedDraftsByProjectId(Long projectId) {
        return postRepository.findNonDeletedDraftsByProjectId(projectId);
    }

    @Transactional
    public List<Post> getNotDeletedPublishedByUserId(Long userId) {
        return postRepository.findNonDeletedPublishedByAuthorId(userId);
    }

    @Transactional
    public List<Post> getNotDeletedPublishedByProjectId(Long projectId) {
        return postRepository.findNonDeletedPublishedByProjectId(projectId);
    }

    private Post getValidPostOrThrowException(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        PostValidation.validatePostDoesNotExist(postOptional.isPresent());
        return postOptional.get();
    }

}
