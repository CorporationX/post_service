package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.redis.RedisUserDto;
import faang.school.postservice.model.redis.RedisUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RedisUserMapper {

    @Mapping(target = "userId", source = "id")
    RedisUser toRedisUser(UserDto userDto);

    @Mapping(target = "id", source = "userId")
    UserDto toUserDto(RedisUser redisUser);

    RedisUserDto toDto(RedisUser redisUser);
}
