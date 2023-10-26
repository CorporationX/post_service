package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.model.redis.RedisUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RedisUserMapper {
    RedisUser toRedisUser(UserDto userDto);

    UserDto toUserDto(RedisUser redisUser);
}
