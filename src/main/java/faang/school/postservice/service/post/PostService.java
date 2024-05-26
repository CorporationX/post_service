package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.UserEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PublisherUsersBan;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PublisherUsersBan publisherUsersBan;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    @Value("${post.maxUnverifiedPosts}")
    private int maxUnverifiedPosts;

    public PostDto createPost(PostDto postDto) {
        validation(postDto);
        Post post = postMapper.toEntity(postDto);
        Post savePost = postRepository.save(post);
        return postMapper.toDto(savePost);
    }

    public PostDto publishedPost(long id) {
        Post post = findPostById(id);

        if (post.isPublished()) {
            throw new DataValidationException("Пост уже опубликован");
        }

        post.setPublished(true);
        Post savePost = postRepository.save(post);
        return postMapper.toDto(savePost);
    }

    public PostDto updatePost(long id, String content) {
        Post post = findPostById(id);
        post.setContent(content);
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto markDeletePost(long id) {
        Post post = findPostById(id);
        if (post.isDeleted()) {
            throw new DataValidationException("Пост уже помечен на удаление");
        }
        post.setDeleted(true);
        return postMapper.toDto(postRepository.save(post));
    }

    public List<PostDto> getPostsNotDeleteByAuthorId(long authorId) {
        return postRepository.findByAuthorId(authorId)
                .stream()
                .filter(post -> !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(post -> postMapper.toDto(post))
                .toList();
    }

    public List<PostDto> getPostsNotDeleteByProjectId(long projectId) {
        return postRepository.findByProjectId(projectId)
                .stream()
                .filter(post -> !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(post -> postMapper.toDto(post))
                .toList();
    }

    public List<PostDto> getPostsPublishedByAuthorId(long authorId) {
        return postRepository.findByAuthorId(authorId)
                .stream()
                .filter(post -> post.isPublished())
                .sorted((p1, p2) -> p2.getPublishedAt().compareTo(p1.getPublishedAt()))
                .map(post -> postMapper.toDto(post))
                .toList();
    }

    public List<PostDto> getPostsPublishedByProjectId(long projectId) {
        return postRepository.findByAuthorId(projectId)
                .stream()
                .filter(post -> post.isPublished())
                .sorted((p1, p2) -> p2.getPublishedAt().compareTo(p1.getPublishedAt()))
                .map(post -> postMapper.toDto(post))
                .toList();
    }

    public PostDto getPostById(long postId) {
        return postMapper.toDto(findPostById(postId));
    }
    public void banUsersWithMultipleUnverifiedPosts() {
        List<Post> posts = getUnverifiedPost();
        sendUsersToBan(posts);
    }

    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id %s not found", postId)));
    }

    public List<Post> findReadyToPublishAndUncorrected() {
        return postRepository.findReadyToPublishAndUncorrected();
    }

    private List<Post> getUnverifiedPost() {
        return postRepository.findByVerified(false);
    }

    private void sendUsersToBan(List<Post> posts) {
        posts.stream()
                .filter(post -> !post.isVerified())
                .collect(Collectors.groupingBy(Post::getAuthorId, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= maxUnverifiedPosts)
                .map(Map.Entry::getKey)
                .forEach(id -> publisherUsersBan.publish(new UserEvent(id)));
    }

    private void validation(PostDto postDto) {
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("У поста должен быть владелец!");
        }
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("У поста должен быть только один владелец либо автор либо проект");
        }
    }

    private UserDto getUser(long id) {
        return userServiceClient.getUser(id);
    }

    private ProjectDto getProject(long id) {
        return projectServiceClient.getProject(id);
    }
}
