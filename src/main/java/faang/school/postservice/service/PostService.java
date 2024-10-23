package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.SpellCheckerDto;
import faang.school.postservice.event.AnalyticsEvent;
import faang.school.postservice.event.EventType;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.PostRequirementsException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.tools.YandexSpeller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;
    private final YandexSpeller yandexSpeller;

    @Transactional
    public Post createDraftPost(Post post) {
        validateAuthorOrProject(post);
        markAsDraft(post);
        return postRepository.save(post);
    }

    @Transactional
    public Post publishPost(Long id) {
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new PostRequirementsException("Post not found"));
        if (existingPost.isPublished()) {
            throw new PostRequirementsException("This post is already published");
        }
        publish(existingPost);
        return postRepository.save(existingPost);
    }

    @Transactional
    public Post updatePost(Long id, String content) {
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new PostRequirementsException("Post not found"));
        updateContent(existingPost, content);
        existingPost.setSpellCheck(false);
        return postRepository.save(existingPost);
    }

    @Transactional
    public Post deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostRequirementsException("Post not found"));
        delete(post);
        return postRepository.save(post);
    }

    @Transactional
    @AnalyticsEvent(EventType.POST_VIEW)
    public Post getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostRequirementsException("Post not found"));
        return post;
    }

    @Transactional(readOnly = true)
    public List<Post> getUserDrafts(long userId) {
        return postRepository.findDraftsByAuthorId(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> getProjectDrafts(long projectId) {
        return postRepository.findDraftsByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<Post> getUserPublishedPosts(long userId) {
        return postRepository.findPublishedByAuthorId(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> getProjectPublishedPosts(long projectId) {
        return postRepository.findPublishedByProjectId(projectId);
    }

    @Transactional
    public void correctAllDraftPosts() {
        List<Post> draftPosts = postRepository.findAllDraftsWithoutSpellCheck();

        draftPosts.forEach(post -> {
            String text = post.getContent();
            List<SpellCheckerDto> checkers = yandexSpeller.checkText(text);
            if (!checkers.isEmpty()) {
                String correctedText = yandexSpeller.correctText(text, checkers);
                post.setContent(correctedText);
            }
            post.setSpellCheck(true);
        });

        postRepository.saveAll(draftPosts);
    }


    public List<Long> getAuthorsWithExcessVerifiedFalsePosts() {
        return postRepository.findAuthorsWithExcessVerifiedFalsePosts();
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

    private void markAsDraft(Post post) {
        post.setPublished(false);
    }

    private void publish(Post post) {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
    }

    private void updateContent(Post post, String content) {
        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());
    }

    private void delete(Post post) {
        post.setDeleted(true);
        post.setPublished(false);
    }
}

