package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostAlreadyPublishedException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Override
    public PostDto getPostById(long postId) {
        Post foundPost = findPostById(postId);
        return postMapper.toPostDto(foundPost);
    }

    @Override
    public List<PostDto> getNotDeletedUserDrafts(long userId) {
        findUserById(userId);
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    public List<PostDto> getNotDeletedProjectDrafts(long projectId) {
        findProjectById(projectId);
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    public List<PostDto> getNotDeletedUserPublished(long userId) {
        findUserById(userId);
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    public List<PostDto> getNotDeletedProjectPublished(long projectId) {
        findProjectById(projectId);
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    public PostDto deletePost(long postId) {
        Post foundPost = findPostById(postId);
        foundPost.setDeleted(true);
        Post deletedPost = postRepository.save(foundPost);
        return postMapper.toPostDto(deletedPost);
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        Long userId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();
        if ((userId == null && projectId == null) || (userId != null && projectId != null)) {
            throw new IllegalArgumentException("Invalid userId %d and project id %d".formatted(userId, projectId));
        }
        if (userId != null) {
            findUserById(userId);
        } else {
            findProjectById(projectId);
        }
        postDto.setDeleted(false);
        postDto.setPublished(false);
        Post postToCreate = postMapper.toPostEntity(postDto);
        Post createdPost = postRepository.save(postToCreate);
        return postMapper.toPostDto(createdPost);
    }

    @Override
    public PostDto publishPost(long postId) {
        Post foundPost = findPostById(postId);
        if (foundPost.isPublished()) {
            throw new PostAlreadyPublishedException("Post with id %d is already published".formatted(postId));
        }
        foundPost.setPublished(true);
        foundPost.setPublishedAt(LocalDateTime.now());
        Post publishedPost = postRepository.save(foundPost);
        return postMapper.toPostDto(publishedPost);
    }

    @Override
    public PostDto updatePost(long postId, PostUpdateDto postUpdateDto) {
        Post foundPost = findPostById(postId);
        postMapper.update(postUpdateDto, foundPost);
        Post updatedPost = postRepository.save(foundPost);
        return postMapper.toPostDto(updatedPost);
    }

    private Post findPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with id %d doesn't exist".formatted(postId)));
    }

    private UserDto findUserById(long userId) {
        return userServiceClient.getUser(userId);
    }

    private ProjectDto findProjectById(long projectId) {
        return projectServiceClient.getProject(projectId);
    }
}