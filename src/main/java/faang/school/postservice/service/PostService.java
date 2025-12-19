package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;

    @Transactional
    public void createPost(PostDto postDto) {
        validateAuthorPresence(postDto);

        Post post = postMapper.toPost(postDto);
        post.setPublished(false);
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        log.info("Create post completed");
    }

    @Transactional
    public void publishPost(long postId) {
        Post post = findPostById(postId);

        if (post.isPublished()) {
            throw new IllegalArgumentException("The post has already been published");
        }

        postRepository.updateIsPublished(post.getId(), true, LocalDateTime.now());

        log.info("The post has been published successfully");
    }

    @Transactional
    public void updatePost(PostDto postDto) {
        Post postBD = findPostById(postDto.getId());
        Post postUser = postMapper.toPost(postDto);

        if (!Objects.equals(postBD.getAuthorId(), postUser.getAuthorId())) {
            postUser.setAuthorId(postBD.getAuthorId());
        } else if (!Objects.equals(postBD.getProjectId(), postUser.getProjectId())) {
            postUser.setProjectId(postBD.getProjectId());
        }

        postUser.setPublishedAt(postBD.getPublishedAt());
        postRepository.deleteById(postBD.getId());
        postRepository.save(postUser);

        log.info("The post has been updated successfully");
    }

    @Transactional
    public void deletePost(long postId) {
        Post post = findPostById(postId);
        postRepository.updateIsDeleted(post.getId(), true);
        log.info("The post has been deleting successfully");
    }

    @Transactional
    public PostDto getPost(long postId) {
        Post post = findPostById(postId);
        log.info("The post was sent to the user");
        return postMapper.toPostDto(post);
    }

    @Transactional
    public List<PostDto> getPostDraftsByAuthorId(long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toPostDto)
                .toList();
    }

    @Transactional
    public List<PostDto> getPostDraftsByProjectId(long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toPostDto)
                .toList();
    }

    @Transactional
    public List<PostDto> getPostPublishedByAuthorId(long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toPostDto)
                .toList();
    }

    @Transactional
    public List<PostDto> getPostPublishedByProjectId(long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toPostDto)
                .toList();
    }

    @Retryable(retryFor = {RuntimeException.class}, maxAttempts = 4, backoff = @Backoff(delay = 1000, multiplier = 2))
    private void validateAuthorPresence(PostDto postDto) {
        log.debug("Checking for the presence of the author in the database");
        try {
            if (postDto.getAuthorId() != null) {
                userServiceClient.getUser(postDto.getAuthorId());
            } else {
                projectServiceClient.getProject(postDto.getProjectId());
            }
        } catch (NullPointerException  e) {
            log.error("Element not found exception:", e);
            throw new NullPointerException("Author with this id will not be found");
        } catch (RuntimeException e) {
            log.error("Feign client error:", e);
            throw new RuntimeException("Unexpected Feign client error", e);
        }
    }

    private Post findPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post will not be found"));
    }
}
