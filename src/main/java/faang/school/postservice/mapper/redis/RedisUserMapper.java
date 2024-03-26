package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.UserDto;
import faang.school.postservice.model.redis.RedisUser;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisUserMapper {

    RedisUser toRedisUser(UserDto userDto);
}
