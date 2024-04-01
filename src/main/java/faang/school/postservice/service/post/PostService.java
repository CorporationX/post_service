package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.post.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final ExecutorService postPublisherThreadPool;

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

    @Transactional
    public List<PostDto> publishScheduledPosts() {
        List<Post> posts = postRepository.findReadyToPublish();
        int postsQuantity = posts.size();
        List<CompletableFuture<List<Post>>> futurePostsPublished = new CopyOnWriteArrayList<>();

        for (int i = 0; i < postsQuantity; i += 1000) {
            final int fromIndex = i;
            int toIndex = Math.min(i + 1000, postsQuantity);

            futurePostsPublished.add(CompletableFuture.supplyAsync(() -> {
                List<Post> batch = posts.subList(fromIndex, toIndex);
                batch.forEach(post -> {
                    post.setPublished(true);
                    post.setPublishedAt(LocalDateTime.now());
                });
                return postRepository.saveAll(batch);
            }, postPublisherThreadPool));
        }

        return futurePostsPublished.stream()
                .flatMap(future -> future.join().stream())
                .map(postMapper::toDto)
                .toList();
    }

    public PostDto update(PostDto postDto) {
        Post post = getPost(postDto.getId());
        postValidator.validateUpdatedPost(post, postDto);
        post.setContent(postDto.getContent());

        return postMapper.toDto(postRepository.save(post));
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
