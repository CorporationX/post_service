package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.PostAlreadyPublishedException;
import faang.school.postservice.exception.PostWasDeletedException;
import faang.school.postservice.exception.ProjectNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostHashtagCacheService postHashtagCacheService;

    private static final String REGEX = "#\\w+";

    @Transactional
    public void createPostByUserId(Long userId, Post post) {
        doesUserExist(userId);
        post.setAuthorId(userId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        findHashtags(post);
        postRepository.save(post);
        postHashtagCacheService.setPostsIntoCache(post);
    }

    @Transactional
    public void createPostByProjectId(Long projectId, Post post) {
        doesProjectExist(projectId);
        post.setProjectId(projectId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        findHashtags(post);
        postRepository.save(post);
        postHashtagCacheService.setPostsIntoCache(post);
    }

    @Transactional
    public void publishPost(Long postId) {
        Post post = getPost(postId);

        if (post.getPublishedAt() != null) {
            throw new PostAlreadyPublishedException("The post has already been published");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(Long postId, Post post) {
        Post existingPost = getPost(postId);

        existingPost.setContent(post.getContent());
        existingPost.setUpdatedAt(LocalDateTime.now());
        existingPost.setProjectId(post.getProjectId());

        findHashtags(existingPost);
        postRepository.save(existingPost);
        postHashtagCacheService.setPostsIntoCache(existingPost);
    }

    @Transactional
    public void softDeletePost(Long postId) {
        Post existingPost = getPost(postId);

        existingPost.setDeleted(true);

        postRepository.save(existingPost);
        postHashtagCacheService.removePostFromCache(existingPost);
    }

    public Post getPostById(Long postId) {
        Post post = getPost(postId);

        if (post.isDeleted()) {
            throw new PostWasDeletedException("The post was deleted");
        }

        return post;
    }

    public List<Post> getNotPublishedPostsByUser(Long userId) {
        doesUserExist(userId);
        return postRepository
                .findByAuthorId(userId).stream()
                .filter(post -> !post.isPublished())
                .collect(Collectors.toList());
    }

    public List<Post> getNotPublishedPostsByProject(Long projectId) {
        doesProjectExist(projectId);
        return postRepository
                .findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished())
                .collect(Collectors.toList());
    }

    public List<Post> getPublishedPostsByUser(Long userId) {
        doesUserExist(userId);
        return postRepository
                .findByAuthorIdWithLikes(userId).stream()
                .filter(Post::isPublished)
                .collect(Collectors.toList());
    }

    public List<Post> getPublishedPostsByProject(Long projectId) {
        doesProjectExist(projectId);
        return postRepository
                .findByProjectIdWithLikes(projectId).stream()
                .filter(Post::isPublished)
                .collect(Collectors.toList());
    }

    @Transactional
    public void savePost(Post post) {
        postRepository.save(post);
    }

    private void findHashtags(Post post) {
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(post.getContent());

        List<String> hashtags = new ArrayList<>();

        while (matcher.find()) {
            hashtags.add(matcher.group());
        }
        post.setHashtags(hashtags);
    }

    private void doesUserExist(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
    }

    private void doesProjectExist(Long projectId) {
        try {
            projectServiceClient.getProject(projectId);
        } catch (FeignException.NotFound e) {
            throw new ProjectNotFoundException("Project with id " + projectId + " not found");
        }
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }
}