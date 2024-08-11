package faang.school.postservice.service;

import faang.school.postservice.client.HashtagServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostContextMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.hashtag.Hashtag;
import faang.school.postservice.model.hashtag.HashtagRequest;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.elasticsearchService.ElasticsearchService;
import faang.school.postservice.validator.PostServiceValidator;
import feign.FeignException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostServiceValidator postServiceValidator;
    private final HashtagServiceClient hashtagServiceClient;
    private final ElasticsearchService elasticsearchService;
    private final EntityManager entityManager;
    private final PostContextMapper context;

    @Transactional
    public PostDto createPost(PostDto postDto) {
        postServiceValidator.validateCreatePost(postDto);
        saveHashtags(postDto.getHashtagNames());
        List<Hashtag> hashtags = getHashtagsByNames(postDto.getHashtagNames());

        Post post = Post.builder()
                .authorId(postDto.getAuthorId())
                .projectId(postDto.getProjectId())
                .content(postDto.getContent())
                .hashtags(hashtags.stream()
                        .map(entityManager::merge)
                        .toList())
                .build();

        post = postRepository.save(post);
        PostDto postDtoForReturns = postMapper.toDto(post);
        elasticsearchService.indexPost(postDtoForReturns);
        return postDtoForReturns;
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Post post = getPostById(postDto.getId());
        postServiceValidator.validateUpdatePost(post, postDto);
        saveHashtags(postDto.getHashtagNames());

        post.setHashtags(new ArrayList<>(getHashtagsByNames(postDto.getHashtagNames())));
        post.setContent(postDto.getContent());

        post = postRepository.save(post);
        PostDto postDtoForReturns = postMapper.toDto(post);
        elasticsearchService.indexPost(postDtoForReturns);
        return postDtoForReturns;
    }

    @Transactional
    public PostDto publishPost(PostDto postDto) {
        Post post = getPostById(postDto.getId());
        postServiceValidator.validatePublishPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto deletePost(Long postId) {
        Post post = getPostById(postId);
        postServiceValidator.validateDeletePost(post);
        post.setDeleted(true);
        if (post.isPublished()) {
            post.setPublished(false);
        }

        post = postRepository.save(post);
        elasticsearchService.removePost(postId);
        return postMapper.toDto(post);
    }

    public PostDto getPostDtoById(Long postId) {
        return postMapper.toDto(getPostById(postId));
    }

    public List<PostDto> getAllDraftPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<Post> filteredPosts = posts.stream()
                .filter(post -> !post.isPublished())
                .toList();

        return postMapper.toDto(sortPostsByCreateAt(filteredPosts));
    }

    public List<PostDto> getAllDraftPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<Post> filteredPosts = posts.stream()
                .filter(post -> !post.isPublished())
                .toList();

        return postMapper.toDto(sortPostsByCreateAt(filteredPosts));
    }

    public List<PostDto> getAllPublishPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<Post> filteredPosts = posts.stream()
                .filter(Post::isPublished)
                .toList();

        return postMapper.toDto(sortPostsByPublishAt(filteredPosts));
    }

    public List<PostDto> getAllPublishPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<Post> filteredPosts = posts.stream()
                .filter(Post::isPublished)
                .toList();

        return postMapper.toDto(sortPostsByPublishAt(filteredPosts));
    }

    public List<PostDto> findPostsByHashtag(String hashtagName) {
        List<PostDto> postDtos = hashtagServiceClient.findPostsByHashtag(hashtagName).getPosts();
        if (!postDtos.isEmpty()) {
            return postDtos;
        } else {
            return elasticsearchService.searchPostsByHashtag(hashtagName);
        }
    }

    public Post getPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post with the same id does not exist"));

        long countLike = post.getLikes().size();
        context.getCountLikeEveryonePost().put(postId, countLike);

        return post;
    }

    private List<Post> sortPostsByCreateAt(List<Post> posts) {
        return posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    private List<Post> sortPostsByPublishAt(List<Post> posts) {
        return posts.stream()
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post ID " + postId + " not found");
                    return new EntityNotFoundException("Post ID " + postId + " not found");
                });
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    private void saveHashtags(List<String> hashtagNames) {
        hashtagServiceClient.saveHashtags(HashtagRequest.builder()
                .hashtagNames(hashtagNames)
                .build());
        log.info("Hashtags have been saved successfully");
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    private List<Hashtag> getHashtagsByNames(List<String> hashtagNames) {
        List<Hashtag> hashtags = hashtagServiceClient.getHashtagsByNames(HashtagRequest.builder()
                .hashtagNames(hashtagNames)
                .build()).getHashtags();
        log.info("Hashtags request was completed successfully");

        return hashtags;
    }
}
