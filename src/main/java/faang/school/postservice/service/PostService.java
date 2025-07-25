package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final RedisService redisService;

    private static final String POSTS_CACHE = "posts:";
    private static final String FEED_CACHE = "feed:";

    @Value("${ttl.posts}")
    private Long ttlPosts;

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no such id = " + id));
    }


    public PostResponseDto getPostDtoById(long id) {
        PostResponseDto postResponseDto = redisService.getPost(POSTS_CACHE + id);
        if (postResponseDto != null) {
            log.info("______________________________________________return out redis_________________________________");
            return postResponseDto;
        }
        log.info("______________________________________________return out bd_________________________________");
        return postMapper.toDto(postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no such id = " + id)));
    }

    public PostResponseDto createDraftPost(PostRequestDto request) {
        Post post = postMapper.toEntity(request);

        postRepository.save(post);

        savePostRedis(post);

        return postMapper.toDto(post);
    }

    public PostResponseDto publishPost(Long postId) {
        Post post = getPostById(postId);
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post already posted.");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostResponseDto updatePost(Long postId, PostRequestDto request) {
        Post post = getPostById(postId);
        post.setContent(request.content());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostResponseDto deletePost(Long postId) {
        Post post = getPostById(postId);
        post.setDeleted(true);
        return postMapper.toDto(postRepository.save(post));
    }

    public List<PostResponseDto> getAllNotDeletedDraftsByAuthorId(Long authorId) {
        return getDraftsById(postRepository.findByAuthorId(authorId));
    }

    public List<PostResponseDto> getAllNotDeletedDraftsByProjectId(Long projectId) {
        return getDraftsById(postRepository.findByProjectId(projectId));
    }

    public List<PostResponseDto> getAllPostsByAuthorId(Long authorId) {
        return getPostsById(postRepository.findByAuthorId(authorId));
    }

    public List<PostResponseDto> getAllPostsByProjectId(Long projectId) {
        return getPostsById(postRepository.findByProjectId(projectId));
    }

    private List<PostResponseDto> getDraftsById(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostResponseDto::getCreatedAt))
                .toList();
    }

    private List<PostResponseDto> getPostsById(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostResponseDto::getPublishedAt))
                .toList();
    }

    private void savePostRedis(Post post) {
        String postsKey = POSTS_CACHE + post.getId();
        PostResponseDto postFeedDto = postMapper.toDto(post);

        redisService.saveToRedisWithTtl(postsKey, postFeedDto, ttlPosts, TimeUnit.DAYS);
        log.info("Post with id = {} save in redis", postFeedDto.getId());

        List<Long> followers = getAllFollowers(post.getAuthorId());
        try {
            followers.forEach(id -> {
                String key = FEED_CACHE + id;

                ConcurrentLinkedDeque<Long> feeds = redisService.getAndDeleteFeed(key);
                feeds.add(postFeedDto.getId());

                redisService.saveToRedis(key, feeds);
            });
            log.info("Feed users = {} is update", followers);

        } catch (ClassCastException e) {
            postRepository.delete(post);
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private List<Long> getAllFollowers(Long id) {
        return postRepository.findAllIdFollowerFollowee(id);
    }

    public List<Post> getPostByFollowerIdWithLimit(Long followerId, Long postId, Long limit) {
        return postRepository.findPostByFollowerId(followerId, postId, limit);
    }
}
