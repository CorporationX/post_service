package faang.school.postservice.redis.cache.service.feed;

import faang.school.postservice.dto.post.PostDto;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedCacheService {

    void addPostIdToFollowerFeed(Long postId, Long subscriberId, LocalDateTime publishedAt);

    List<PostDto> getFeedByUserId(Long userId, Long postId);
}