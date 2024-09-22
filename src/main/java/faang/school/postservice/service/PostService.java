package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.PostRequirementsException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;

    @Transactional
    public Post createDraftPost(Post post) {
        validateAuthorOrProject(post);
        post.markAsDraft();
        return postRepository.save(post);
    }

    @Transactional
    public Post publishPost(Long id) {
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new PostRequirementsException("Post not found"));
        if (existingPost.isPublished()) {
            throw new PostRequirementsException("This post is already published");
        }
        existingPost.publish();
        return postRepository.save(existingPost);
    }

    @Transactional
    public Post updatePost(Post post) {
        Post existingPost = postRepository.findById(post.getId()).orElseThrow(() -> new PostRequirementsException("Post not found"));
        existingPost.updateContent(post.getContent());
        return postRepository.save(existingPost);
    }

    @Transactional
    public Post deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostRequirementsException("Post not found"));
        post.delete();
        return postRepository.save(post);
    }

    private void validateAuthorOrProject(Post post) {
        if (Objects.nonNull(post.getAuthorId()) && Objects.nonNull(post.getProjectId())) {
            throw new DataValidationException("The post can't be made by both a user and a project at the same time.");
        }

        if (Objects.isNull(post.getAuthorId()) && Objects.isNull(post.getProjectId())) {
            throw new DataValidationException("The post must have either a user or a project as an author.");
        }

        if (Objects.nonNull(post.getAuthorId())) {
            validateUserExists(post);
        } else {
            validateProjectExists(post);
        }
    }

    private void validateUserExists(Post post) {
        userContext.setUserId(post.getAuthorId());
        userServiceClient.getUser(post.getAuthorId());
    }

    private void validateProjectExists(Post post) {
        projectServiceClient.getProject(post.getProjectId());
    }
}

