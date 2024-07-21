package faang.school.postservice.service.post;

import faang.school.postservice.dto.moderation.ModerationDictionary;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final ExecutorService threadPool;

    @Value("${post.publisher.batch-size}")
    private Integer scheduledPostsBatchSize;

    @Value("${moderation.chunkSize}")
    private int chunkSize;

    @Override
    @Async("moderatorExecutor")
    public void moderatePosts() {
        List<Post> moderatingPosts = postRepository.findAllByVerifiedAtIsNull();
        CompletableFuture.supplyAsync(() -> createSubLists(moderatingPosts))
                .thenAcceptAsync(posts -> posts.stream()
                        .flatMap(Collection::stream)
                        .forEach(post -> {
                            post.setVerified(moderationDictionary.inspect(post.getContent()));
                            post.setVerifiedAt(LocalDateTime.now());
                        }));
    }

    @Override
    public Post getPostById(long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Post with ID " + id + " not found"));
    }

    private List<List<Post>> createSubLists(List<Post> posts) {
        List<List<Post>> chunks = new ArrayList<>();
        for (int index = 0; index < posts.size(); index += chunkSize) {
            List<Post> chunk = new ArrayList<>(posts.subList(index, Math.min(posts.size(), index + chunkSize)));
            chunks.add(chunk);
        }
        return chunks;
    }

    private List<PostDto> getNotDeletedPosts(List<Post> posts, Predicate<Post> predicate) {
        return posts.stream()
                .filter(predicate)
                .filter(post -> !post.isDeleted())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public PostDto createPost(PostDto postDto) {
        postValidator.validateAuthorIdAndProjectId(postDto.getAuthorId(), postDto.getProjectId());
        Post post = postRepository.save(postMapper.toEntity(postDto));
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publishPost(Long postId) {
        Post post = findById(postId);
        postValidator.validatePublicationPost(post);
        post.setPublished(true);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(Long postId, String content, LocalDateTime publicationTime) {
        Post post = findById(postId);
        post.setContent(content);
        post.setPublishedAt(publicationTime);
        return postMapper.toDto(post);
    }

    @Transactional
    public void deletePostById(Long postId) {
        Post post = findById(postId);
        post.setDeleted(true);
    }

    @Transactional
    public PostDto getPost(Long postId) {
        Post post = findById(postId);
        return postMapper.toDto(post);
    }

    @Transactional
    public List<PostDto> getAllPostsDraftsByUserAuthorId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<PostDto> draftsPostsByUser = getNotDeletedPosts(posts, (post -> !post.isPublished()));
        return draftsPostsByUser;
    }

    @Transactional
    public List<PostDto> getAllPostsDraftsByProjectAuthorId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<PostDto> draftsPostsByProject = getNotDeletedPosts(posts, (post -> !post.isPublished()));
        return draftsPostsByProject;
    }

    @Transactional
    public List<PostDto> getAllPublishedNotDeletedPostsByUserAuthorId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<PostDto> publishedPostsByUser = getNotDeletedPosts(posts, (Post::isPublished));
        return publishedPostsByUser;
    }

    @Transactional
    public List<PostDto> getAllPublishedNotDeletedPostsByProjectAuthorId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<PostDto> publishedPostsByProject = getNotDeletedPosts(posts, (Post::isPublished));
        return publishedPostsByProject;
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() ->
                        new DataValidationException(String.format("Post with this Id: %s was not found", postId)));
    }

    @Transactional
    public void publishScheduledPosts() {
        List<Post> posts = postRepository.findReadyToPublish();
        IntStream.iterate(0, i -> i < posts.size(), i -> i + scheduledPostsBatchSize)
                .mapToObj(i -> posts.subList(i, Math.min(i + scheduledPostsBatchSize, posts.size())))
                .toList()
                .forEach(batch -> CompletableFuture.runAsync(() -> {
                    batch.forEach(post -> {
                        post.setPublished(true);
                        post.setPublishedAt(LocalDateTime.now());
                    });
                }, threadPool));
    }

    @Override
    public boolean existsById(long id) {
        return postRepository.existsById(id);
    }
}
