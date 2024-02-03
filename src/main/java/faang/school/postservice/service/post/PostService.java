package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final PostRepository postRepository;

    @Transactional
    public Post createDraft(PostDto postDto) {
        if (postDto.getAuthorId() == null && postDto.getProjectId() != null) {
            projectServiceClient.getProject(postDto.getProjectId());
        }
        if (postDto.getProjectId() == null && postDto.getAuthorId() != null) {
            userServiceClient.getUser(postDto.getAuthorId());
        } else {
            throw new DataValidationException("Incorrect author");
        }
        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
        return post;
    }

    @Transactional
    public void publish(long postId) {
        Post post = searchPostById(postId);
        if (post.isPublished()) {
            throw new IllegalArgumentException("The post has already been published");
        }
        post.setPublished(true);
        post.setDeleted(false);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Transactional
    public void update(PostDto postDto) {
        Post post = searchPostById(postDto.getId());
        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Transactional
    public void removeSoftly(long id) {
        Post post = searchPostById(id);
        post.setPublished(false);
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Transactional
    public PostDto getPostById(long id) {
        Post post = searchPostById(id);
        return postMapper.toPostDto(post);
    }

    @Transactional
    public List<PostDto> getDraftsByAuthorId(long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        return filter(posts, false, false);
    }

    @Transactional
    public List<PostDto> getDraftsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return filter(posts, false, false);

    }

    @Transactional
    public List<PostDto> getPublishedPostsByAuthor(long authorId){
        List<Post> posts = postRepository.findByAuthorId(authorId);
        return posts.stream()
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toPostDto)
                .toList();
    }

    private Post searchPostById(long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new EntityNotFoundException("Post with id " + id + " not found.");
        }
        return optionalPost.get();
    }

    private List<PostDto> filter(List<Post> posts, boolean isPublished, boolean isDeleted){
        return posts.stream()
                .filter(post -> post.isPublished() == isPublished)
                .filter(post -> post.isDeleted() == isDeleted)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toPostDto)
                .toList();
    }
}
