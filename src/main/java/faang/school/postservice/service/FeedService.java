package faang.school.postservice.service;

import faang.school.postservice.redis.model.dto.FeedDto;

public interface FeedService {
    FeedDto getFeed (Long feedId, Long userId, Integer startPostId);
}
