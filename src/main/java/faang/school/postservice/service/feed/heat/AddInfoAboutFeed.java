package faang.school.postservice.service.feed.heat;

import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
@Order(3)
public class AddInfoAboutFeed implements HeatFeed {
    private final RedisCacheService redisCacheService;
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public void addInfoToRedis(Long userId, Long postId) {
        Timestamp updatedTime = postRepository.getUpdatedTime(postId);
        redisCacheService.addPostToUserFeed(postId, userId, updatedTime.getTime());
    }
}
