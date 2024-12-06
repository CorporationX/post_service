package faang.school.postservice.mapper.redis.user;

import faang.school.postservice.dto.redis.UserRedis;
import faang.school.postservice.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDtoToUserRedisMapper {
    UserDto toUserDto(UserRedis userRedis);
    UserRedis toUserRedis(UserDto userDto);
}
