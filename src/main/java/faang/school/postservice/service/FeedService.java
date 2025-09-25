package faang.school.postservice.service;

import faang.school.postservice.dto.response.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.FollowerRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final PostCacheService postCacheService;
    private final FollowerRepository followerRepository;

    public List<Post> getUserFeed(Long userId) {
        log.info("Fetching feed for user ID: {}", userId);

        List<Post> cachedFeed = postCacheService.getCachedUserFeed(userId);
        if (!cachedFeed.isEmpty()) {
            log.debug("Loaded feed for user {} from cache", userId);
            return cachedFeed;
        }

        List<Long> followeeIds = followerRepository.findFollowerIdsByUserId(userId);

        if (followeeIds.isEmpty()) {
            log.debug("User {} has no followees, returning empty feed", userId);
            return Collections.emptyList();
        }

        List<Post> posts = postRepository.findPostsByAuthorIds(followeeIds);

        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        postCacheService.cacheUserFeed(userId, posts);

        return posts;
    }

    public List<Post> getTrendingPosts(int limit) {
        log.info("Fetching {} trending posts", limit);

        List<Post> cached = postCacheService.getTrendingPosts(limit);
        if (!cached.isEmpty()) {
            return cached;
        }

        List<Post> trending = postRepository.findTrendingPosts();

        if (trending.size() > limit) {
            trending = trending.subList(0, limit);
        }

        int score = trending.size();
        for (Post post : trending) {
            postCacheService.addToTrendingPosts(post, score--);
        }

        return trending;
    }

    public List<PostDto> getUserFeed(Long userId, Long afterPostId) {
        List<Long> feedIds = postCacheService.getUserFeedIds(userId, afterPostId, 20);

        if (feedIds.isEmpty()) {
            return postRepository.findUserFeed(userId).stream()
                    .map(this::toDto)
                    .toList();
        }

        List<PostDto> result = new ArrayList<>();
        for (Long postId : feedIds) {
            Post post = postCacheService.getCachedPost(postId).orElse(null);
            if (post == null) {
                post = postRepository.findById(postId).orElse(null);
            }
            if (post != null) {
                result.add(toDto(post));
            }
        }

        return result;
    }

    private PostDto toDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .authorName("TODO: из Redis users:<id> или из UserService")
                .projectId(post.getProjectId())
                .published(post.isPublished())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
