package faang.school.postservice.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service responsible for managing user news feeds.
 *
 * <p>Feed data is stored in Redis using a {@code ZSET} structure:
 * <ul>
 *     <li><b>Key</b>: {@code feed:v1:{followerId}}</li>
 *     <li><b>Member</b>: {@code postId}</li>
 *     <li><b>Score</b>: post creation timestamp ({@link Instant})</li>
 * </ul>
 *
 * <p>Redis is used as a fast-access cache layer for feeds.
 * Only post identifiers are stored here; full post content
 * is fetched from the database or another service when needed.
 *
 * <p>The feed has a fixed maximum size (e.g. 500 posts).
 * When the limit is exceeded, the oldest posts are removed.
 */
@Service
public interface FeedService {

    /**
     * Adds a post to a follower's feed.
     *
     * <p>The operation is idempotent: if the same post is processed
     * multiple times (e.g. due to Kafka redelivery), it will not be duplicated
     * in the feed.
     *
     * <p>Posts are ordered by creation time, with the newest posts
     * appearing at the top of the feed.
     *
     * @param followerId id of the user whose feed is being updated
     * @param postId id of the newly published post
     * @param occurredAt timestamp when the post was created/published
     */
    void addPostToFeed(long followerId, long postId, Instant occurredAt);

    /**
     * Returns the most recent posts from a follower's feed.
     *
     * <p>Posts are returned in reverse chronological order
     * (newest first).
     *
     * @param followerId id of the user whose feed is requested
     * @param limit maximum number of posts to return
     * @return list of post ids ordered from newest to oldest
     */
    List<Long> getLatestPosts(long followerId, int limit);
}
