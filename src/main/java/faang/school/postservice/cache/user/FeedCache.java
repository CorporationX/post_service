package faang.school.postservice.cache.user;

import faang.school.postservice.dto.feed.FeedRequest;
import faang.school.postservice.dto.post.PostDto;

import java.time.Instant;
import java.util.List;

public interface FeedCache {
    void add(long followerId, long postId, Instant createdAt);

    void addAll(List<Long> followersIds, long postId, Instant createdAt);

    List<PostDto> getUserFeed(long userId, FeedRequest request, int perPage);
}
