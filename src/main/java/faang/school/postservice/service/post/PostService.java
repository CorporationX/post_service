package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.hhzuserban.dto.message.UserBanMessage;
import faang.school.postservice.hhzuserban.publisher.MessagePublisher;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.post.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final MessagePublisher userBanPublisher;

    @Value("${post.banner.post-count}")
    private Integer postsCountToBan;

    public PostDto create(PostDto postDto) {
        postValidator.validatePostAuthor(postDto);
        postValidator.validateIfAuthorExists(postDto);

        Post savedPost = postRepository.save(postMapper.toEntity(postDto));
        return postMapper.toDto(savedPost);
    }

    public PostDto getPostById(long postId) {
        Post post = getPost(postId);
        return postMapper.toDto(post);
    }

    public PostDto publish(long postId) {
        Post post = getPost(postId);
        postValidator.validateIfPostIsPublished(post);
        postValidator.validateIfPostIsDeleted(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto update(PostDto postDto) {
        Post post = getPost(postDto.getId());
        postValidator.validateUpdatedPost(post, postDto);
        post.setContent(postDto.getContent());

        return postMapper.toDto(postRepository.save(post));
    }

    public void banUsers() {
        postRepository.findByVerifiedFalse().stream()
                .collect(Collectors.groupingBy(Post::getAuthorId, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= postsCountToBan)
                .map(Map.Entry::getKey)
                .forEach(userId -> userBanPublisher.publish(new UserBanMessage(userId)));
    }

    public void delete(long postId) {
        Post post = getPost(postId);
        postValidator.validateIfPostIsDeleted(post);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public List<PostDto> getCreatedPostsByUserId(long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .toList();
        return postMapper.toDto(posts);
    }

    public List<PostDto> getCreatedPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .toList();
        return postMapper.toDto(posts);
    }

    public List<PostDto> getPublishedPostsByUserId(long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted((post1, post2) -> post2.getPublishedAt().compareTo(post1.getPublishedAt()))
                .toList();
        return postMapper.toDto(posts);
    }

    public List<PostDto> getPublishedPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted((post1, post2) -> post2.getPublishedAt().compareTo(post1.getPublishedAt()))
                .toList();
        return postMapper.toDto(posts);
    }

    private Post getPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post doesn't exist by id: " + postId));
    }
}
