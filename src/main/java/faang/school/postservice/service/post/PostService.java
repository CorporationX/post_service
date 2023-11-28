package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostAchievementPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final PostAchievementPublisher postAchievementPublisher;
    @Value("${author_banner.count_offensive_content_for_ban}")
    private long countOffensiveContentForBan;

    public Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("This post was not found"));
    }

    @Transactional(readOnly = true)
    public List<Long> getByPostIsVerifiedFalse() {
        return postRepository.findByVerifiedIsFalse().stream()
                .collect(Collectors.groupingBy(Post::getAuthorId, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > countOffensiveContentForBan)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Transactional
    public void createPost(PostDto postDto) {
        definitionId(postDto);
        postRepository.save(postMapper.toEntity(postDto));
        postAchievementPublisher.publish(postMapper.toEventDto(postDto));
    }

    @Transactional
    public void publishPost(long postId, long userId) {
        Post post = getPostById(postId);
        postValidator.validatePostByUser(post, userId);
        postValidator.isPublished(post);
        post.setPublished(true);
    }

    @Transactional
    public void publishPostByProject(long postId, long projectId) {
        Post post = getPostById(postId);
        postValidator.validatePostByProject(post, projectId);
        postValidator.isPublished(post);
        post.setPublished(true);
    }

    @Transactional
    public void updatePost(long postId, PostDto postDto) {
        Post post = getPostById(postId);
        definitionId(postDto);
        postValidator.validatePostToUpdate(post, postDto);

        postRepository.save(postMapper.toEntity(postDto));
    }

    @Transactional
    public void deletePost(long postId, long userId) {
        Post post = getPostById(postId);
        postValidator.validatePostByUser(post, userId);
        postValidator.isDeleted(post);
        post.setDeleted(true);

        postRepository.save(post);
    }

    @Transactional
    public void deletePostByProject(long postId, long projectId) {
        Post post = getPostById(postId);
        postValidator.validatePostByProject(post, projectId);
        postValidator.isDeleted(post);
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Transactional
    public PostDto getPost(long postId) {
        Post post = getPostById(postId);
        postValidator.isDeleted(post);

        return postMapper.toDto(post);
    }

    @Transactional
    public List<PostDto> getAllUsersDrafts(long userId) {
        postValidator.validateUser(userId);
        List<Post> posts = postRepository.findAllUsersDrafts(userId);
        List<Post> filteredPosts = posts.stream().sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).toList();

        return postMapper.toDtoList(filteredPosts);
    }

    @Transactional
    public List<PostDto> getAllProjectDrafts(long projectId) {
        postValidator.validateProject(projectId);
        List<Post> posts = postRepository.findAllProjectDrafts(projectId);
        List<Post> filteredPosts = posts.stream().sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).toList();

        return postMapper.toDtoList(filteredPosts);
    }

    @Transactional
    public List<PostDto> getAllUsersPublished(long userId) {
        postValidator.validateUser(userId);
        List<Post> posts = postRepository.findAllAuthorPublished(userId);
        List<Post> filteredPosts = posts.stream().sorted((a, b) -> b.getPublishedAt().compareTo(a.getPublishedAt())).toList();

        return postMapper.toDtoList(filteredPosts);
    }

    @Transactional
    public List<PostDto> getAllProjectPublished(long projectId) {
        postValidator.validateProject(projectId);
        List<Post> posts = postRepository.findAllProjectPublished(projectId);
        List<Post> filteredPosts = posts.stream().sorted((a, b) -> b.getPublishedAt().compareTo(a.getPublishedAt())).toList();

        return postMapper.toDtoList(filteredPosts);
    }

    private void definitionId(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            postValidator.validateUser(postDto.getAuthorId());
        } else {
            postValidator.validateProject(postDto.getProjectId());
        }
    }
}