package faang.school.postservice.heater;

import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Order(3)
@Service
@RequiredArgsConstructor
public class FeedProducer implements Heater {
    private final RedisCache redisCacheService;
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public void addInfoToRedis(Long userId, Long postId) {
        Timestamp updatedTime = postRepository.getUpdatedTime(postId);
        redisCacheService.addPostToUserFeed(postId, userId, updatedTime.getTime());
    }
}
