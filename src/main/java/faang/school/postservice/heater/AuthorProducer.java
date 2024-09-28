package faang.school.postservice.heater;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.post.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Order(1)
@Service
@RequiredArgsConstructor
public class AuthorProducer implements Heater{
    private final UserServiceClient userServiceClient;
    private final RedisCache redisCache;

    @Value("${spring.data.redis.directory.infoAboutAuthor}")
    private String pattern;

    @Override
    public void addInfoToRedis(Long userId, Long postId) {
        UserDto userDto = userServiceClient.getUserByPostId(postId);

        redisCache.saveToCache(pattern, userDto.getId(), userDto);
    }
}
