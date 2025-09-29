package faang.school.postservice.cache.feed;

import java.time.Instant;
import java.util.List;

public interface FeedCache {
    void add(long followerId, long postId, Instant publishedAt);
    void addAll(List<Long> followerIds, long postId, Instant publishedAt);
    List<Long> getIds(long userId, Long afterPostId, int limit);
    void addAllPostsForUser(long userId, List<Long> postIds, List<Instant> publishedAts);
}
