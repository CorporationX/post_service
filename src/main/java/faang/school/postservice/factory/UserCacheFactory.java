package faang.school.postservice.factory;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.redis.UserCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCacheFactory {

    private final UserMapper userMapper;

    @Value("${cache.user.ttl-seconds}")
    private long ttl;


    public UserCache fromUserDto(UserDto user) {
        UserCache cache = userMapper.toUserCache(user);
        cache.setTtl(ttl);
        return cache;
    }
}
