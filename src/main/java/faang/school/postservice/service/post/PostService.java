package faang.school.postservice.service.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.elasticsearchService.ElasticsearchService;
import faang.school.postservice.service.hashtag.HashtagService;
import faang.school.postservice.validator.PostServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostServiceValidator postServiceValidator;
    private final RedisTemplate<String, Serializable> redisTemplate;
    private final ElasticsearchService elasticsearchService;
    private final HashtagService hashtagService;
    private final ObjectMapper objectMapper;

    public PostDto createPost(PostDto postDto) {
        postServiceValidator.validateCreatePost(postDto);
        postDto.getHashtagNames().forEach(hashtagService::saveHashtag);

        Post post = Post.builder()
                .authorId(postDto.getAuthorId())
                .projectId(postDto.getProjectId())
                .content(postDto.getContent())
                .hashtags(postDto.getHashtagNames().stream()
                        .map(hashtagService::getHashtag)
                        .toList())
                .build();

        Post savedPost = post = postRepository.save(post);
        elasticsearchService.indexPost(savedPost);
        return postMapper.toDto(post);
    }

    public PostDto updatePost(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        postServiceValidator.validateUpdatePost(post, postDto);

        postDto.getHashtagNames().forEach(hashtagService::saveHashtag);

        post.setContent(postDto.getContent());
        post.setHashtags(new ArrayList<>(postDto.getHashtagNames().stream()
                .map(hashtagService::getHashtag)
                .toList()));

        post = postRepository.save(post);
        elasticsearchService.indexPost(post);
        return postMapper.toDto(post);
    }

    public PostDto publishPost(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        postServiceValidator.validatePublishPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        post = postRepository.save(post);
        elasticsearchService.indexPost(post);
        return postMapper.toDto(post);
    }

    public PostDto deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        postServiceValidator.validateDeletePost(post);
        post.setDeleted(true);
        if (post.isPublished()) {
            post.setPublished(false);
        }

        post = postRepository.save(post);
        elasticsearchService.removePost(postId);
        return postMapper.toDto(post);
    }

    public List<PostDto> findPostsByHashtag(HashtagDto hashtagDto) {
        List<?> cachedPosts = (List<?>) redisTemplate.opsForValue().get(hashtagDto.getName());
        if (cachedPosts != null) {
            List<Post> posts = cachedPosts.stream()
                    .map(map -> objectMapper.convertValue(map, Post.class))
                    .toList();
            List<PostDto> postDtos = posts.stream()
                    .map(post -> {
                        PostDto postDto = postMapper.toDto(post);
                        List<String> hashtagNames = post.getHashtags().stream()
                                .map(Hashtag::getName)
                                .toList();
                        postDto.setHashtagNames(hashtagNames);
                        return postDto;
                    })
                    .toList();
            return postDtos;
        }

        List<Post> posts = elasticsearchService.searchPostsByHashtag(hashtagDto.getName());
        return postMapper.toDto(posts);
    }

    public PostDto getPostByPostId(Long postId) {
        return postMapper.toDto(postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found")));
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
}