package faang.school.postservice.service.feed;

import faang.school.postservice.dto.redis.PostRedis;

import java.util.List;
import java.util.Set;

public interface FeedService {
    List<PostRedis> getPosts(Long userId);
}
