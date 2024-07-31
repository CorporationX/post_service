package faang.school.postservice.service.feed.heat;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(1)
public class AddInfoAboutAuthor implements HeatFeed {
    private final UserServiceClient userServiceClient;
    private final RedisCacheService redisCacheService;

    @Value("${spring.data.redis.directory.infAuthor}")
    private String patternByInfAuthor;

    @Override
    public void addInfoToRedis(Long userId, Long postId) {
        UserDto userDto = userServiceClient.getUserByPostId(postId);

        redisCacheService.saveToCache(patternByInfAuthor, userDto.getId(), userDto);
    }
}
