package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.UserPostsDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostAlreadyPublishedException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.MessagePublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    @Qualifier(value = "redisUserPublisher")
    private final MessagePublisher userPublisher;

    @Value("${entity.post.max-unverified-count-for-ban}")
    private long maxUnverifiedPostsForBan;

    @Override
    public PostOutputDto getPostById(long postId) {
        Post foundPost = findPostById(postId);
        return postMapper.toPostDto(foundPost);
    }

    @Override
    public List<PostOutputDto> getNotDeletedUserDrafts(long userId) {
        findUserById(userId);
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    public List<PostOutputDto> getNotDeletedProjectDrafts(long projectId) {
        findProjectById(projectId);
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    public List<PostOutputDto> getNotDeletedUserPublished(long userId) {
        findUserById(userId);
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    public List<PostOutputDto> getNotDeletedProjectPublished(long projectId) {
        findProjectById(projectId);
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    @Transactional
    public PostOutputDto deletePost(long postId) {
        Post foundPost = findPostById(postId);
        foundPost.setDeleted(true);
        Post deletedPost = postRepository.save(foundPost);
        return postMapper.toPostDto(deletedPost);
    }

    @Override
    @Transactional
    public PostOutputDto createPost(PostCreateDto postCreateDto) {
        Long userId = postCreateDto.getAuthorId();
        Long projectId = postCreateDto.getProjectId();
        if ((userId == null && projectId == null) || (userId != null && projectId != null)) {
            throw new IllegalArgumentException("Invalid userId %d and project id %d".formatted(userId, projectId));
        }
        if (userId != null) {
            findUserById(userId);
        } else {
            findProjectById(projectId);
        }
        Post postToCreate = postMapper.toPostEntity(postCreateDto);
        postToCreate.setDeleted(false);
        postToCreate.setPublished(false);
        Post createdPost = postRepository.save(postToCreate);
        return postMapper.toPostDto(createdPost);
    }

    @Override
    @Transactional
    public PostOutputDto publishPost(long postId) {
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
    @Transactional
    public PostOutputDto updatePost(long postId, PostUpdateDto postUpdateDto) {
        Post foundPost = findPostById(postId);
        postMapper.update(postUpdateDto, foundPost);
        Post updatedPost = postRepository.save(foundPost);
        return postMapper.toPostDto(updatedPost);
    }

    public void publishUsersToBan() {
        List<Long> usersIds = this.getUsersIdsToBan();
        usersIds.stream()
                .map(String::valueOf)
                .forEach(userPublisher::publish);
    }

    private List<Long> getUsersIdsToBan() {
        List<UserPostsDto> postsCount = postRepository.findUnverifiedPostsCountForUsers(maxUnverifiedPostsForBan);
        return postsCount.stream()
                .map(UserPostsDto::getAuthorId)
                .toList();
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