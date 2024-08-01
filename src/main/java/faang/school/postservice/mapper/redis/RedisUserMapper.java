package faang.school.postservice.mapper.redis;

import faang.school.postservice.entity.dto.user.UserDto;
import faang.school.postservice.entity.model.redis.RedisUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisUserMapper {

    RedisUser toEntity(UserDto userDto);

    RedisUser toRedisDto(UserDto userDto);
}
