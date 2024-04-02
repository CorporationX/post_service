package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.redis.RedisUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RedisUserMapper {

    RedisUser toRedisEntity(UserDto user);
}
