package faang.school.postservice.service;

import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.event.kafka.PostPublishedKafkaEvent;

import java.util.List;

public interface FeedService {

    void addPost(PostPublishedKafkaEvent event);

    List<RedisPostDto> getNewsFeed(Long userId, int page, int pageSize);

    void startHeatingInBackground();
}
