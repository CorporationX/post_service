package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.FeedCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedHeaterService {

    private final FeedCacheService feedCacheService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    @Value("${app.feed.heat.feed-size:500}")
    private int feedSize;

    /**
     * Heats feeds for list of users (called from Kafka consumer).
     * Supports idempotency via jobId.
     */
    @Transactional(readOnly = true)
    public void heatFeedForUsers(List<Long> userIds, String jobId) {
        log.info("Heating feed for {} users (job: {})", userIds.size(), jobId);
        
        int processed = 0;
        int skipped = 0;
        
        for (Long userId : userIds) {
            try {
                if (feedCacheService.isFeedHeatedForJob(userId, jobId)) {
                    log.debug("Feed for user {} already heated in job {}, skipping", userId, jobId);
                    skipped++;
                    continue;
                }
                
                heatFeedForUser(userId);

                feedCacheService.markFeedHeatedForJob(userId, jobId);
                
                processed++;
            } catch (Exception e) {
                log.error("Error heating feed for user {}", userId, e);
            }
        }
        
        log.info("Completed heating for {}/{} users (skipped: {}) in job {}", 
                processed, userIds.size(), skipped, jobId);
    }

    /**
     * Heats feed cache for single user.
     * Gets posts from authors that user is subscribed to.
     */
    @Transactional(readOnly = true)
    public void heatFeedForUser(Long userId) {
        try {
            List<Long> subscribedAuthorIds = userServiceClient.getSubscriptions(userId);
            
            if (subscribedAuthorIds.isEmpty()) {
                log.debug("User {} has no subscriptions, skipping", userId);
                return;
            }

            List<Post> feedPosts = postRepository
                .findLatestPublishedByAuthorIds(subscribedAuthorIds, feedSize);
            
            if (feedPosts.isEmpty()) {
                log.debug("No posts found for user {} subscriptions", userId);
                return;
            }

            feedCacheService.saveUserFeed(userId, feedPosts);

            Set<Long> authorIds = feedPosts.stream()
                .map(Post::getAuthorId)
                .collect(Collectors.toSet());

            saveAuthors(authorIds);

            savePostsWithDetailsBatch(feedPosts);
            
            log.debug("Heated feed for user {}: {} posts", userId, feedPosts.size());
            
        } catch (Exception e) {
            log.error("Error heating feed for user {}", userId, e);
            throw new RuntimeException("Failed to heat feed for user " + userId, e);
        }
    }

    private void saveAuthors(Set<Long> authorIds) {
        if (authorIds.isEmpty()) {
            return;
        }
        
        try {
            List<UserDto> users = userServiceClient.getUsersByIds(new ArrayList<>(authorIds));
            users.forEach(feedCacheService::saveUser);
        } catch (Exception e) {
            log.error("Error fetching users for feed heating", e);
        }
    }

    private void savePostsWithDetailsBatch(List<Post> posts) {
        if (posts.isEmpty()) {
            return;
        }
        
        try {
            List<Long> postIds = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

            Map<Long, List<Comment>> commentsByPostId = commentRepository
                .findAllByPostIdIn(postIds)
                .stream()
                .filter(c -> c.getPost() != null)
                .collect(Collectors.groupingBy(c -> c.getPost().getId()));

            Map<Long, List<Like>> likesByPostId = likeRepository
                .findAllByPostIdIn(postIds)
                .stream()
                .filter(l -> l.getPost() != null)
                .collect(Collectors.groupingBy(l -> l.getPost().getId()));

            for (Post post : posts) {
                List<Comment> comments = commentsByPostId
                    .getOrDefault(post.getId(), List.of());
                List<Like> likes = likesByPostId
                    .getOrDefault(post.getId(), List.of());
                
                feedCacheService.savePost(post, comments, likes);
            }
            
        } catch (Exception e) {
            log.error("Error saving posts batch", e);
        }
    }
}
