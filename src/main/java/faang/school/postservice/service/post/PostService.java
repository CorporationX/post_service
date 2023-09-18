package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    @Value("${author_banner.count_offensive_content_for_ban}")
    private long countOffensiveContentForBan;

    public Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("This post was not found"));
    }

    public List<Long> getByPostIsVerifiedFalse() {
        return postRepository.findByVerifiedIsFalse().stream()
                .collect(Collectors.groupingBy(Post::getAuthorId, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > countOffensiveContentForBan)
                .map(Map.Entry::getKey)
                .toList();
    }

    public void createPost(PostDto postDto) {
        idDefinition(postDto);
        postRepository.save(postMapper.toEntity(postDto));
    }

    public void publishPost(long postId, long userId) {
        Post post = getPostById(postId);
        postValidator.validatePostByUser(post, userId);
        postValidator.isPublished(post);
        post.setPublished(true);

        postRepository.save(post);
    }

    public void publishPostByProject(long postId, long projectId) {
        Post post = getPostById(postId);
        postValidator.validatePostByProject(post, projectId);
        postValidator.isPublished(post);
        post.setPublished(true);

        postRepository.save(post);
    }


    public void updatePost(long postId, PostDto postDto) {
        Post post = getPostById(postId);
        idDefinition(postDto);
        postValidator.validatePostToUpdate(post, postDto);

        postRepository.save(postMapper.toEntity(postDto));
    }

    public void deletePost(long postId, long userId) {
        Post post = getPostById(postId);
        postValidator.validatePostByUser(post, userId);
        postValidator.isDeleted(post);
        post.setDeleted(true);

        postRepository.save(post);
    }

    public void deletePostByProject(long postId, long projectId) {
        Post post = getPostById(postId);
        postValidator.validatePostByProject(post, projectId);
        postValidator.isDeleted(post);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public PostDto getPost(long postId) {
        Post post = getPostById(postId);
        postValidator.isDeleted(post);

        return postMapper.toDto(post);
    }

    public List<PostDto> getAllUsersDrafts(long userId) {
        postValidator.validateUser(userId);
        List<Post> posts = postRepository.findAllUsersDrafts(userId);
        List<Post> filteredPosts = posts.stream().sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).toList();

        return postMapper.toDtoList(filteredPosts);
    }

    public List<PostDto> getAllProjectDrafts(long projectId) {
        postValidator.validateProject(projectId);
        List<Post> posts = postRepository.findAllProjectDrafts(projectId);
        List<Post> filteredPosts = posts.stream().sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).toList();

        return postMapper.toDtoList(filteredPosts);
    }

    public List<PostDto> getAllUsersPublished(long userId) {
        postValidator.validateUser(userId);
        List<Post> posts = postRepository.findAllAuthorPublished(userId);
        List<Post> filteredPosts = posts.stream().sorted((a, b) -> b.getPublishedAt().compareTo(a.getPublishedAt())).toList();

        return postMapper.toDtoList(filteredPosts);
    }

    public List<PostDto> getAllProjectPublished(long projectId) {
        postValidator.validateProject(projectId);
        List<Post> posts = postRepository.findAllProjectPublished(projectId);
        List<Post> filteredPosts = posts.stream().sorted((a, b) -> b.getPublishedAt().compareTo(a.getPublishedAt())).toList();

        return postMapper.toDtoList(filteredPosts);
    }

    private void idDefinition(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            postValidator.validateUser(postDto.getAuthorId());
        } else {
            postValidator.validateProject(postDto.getProjectId());
        }
    }
}