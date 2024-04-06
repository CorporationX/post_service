package faang.school.postservice.mapper;

import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.redis.UserRedisDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserRedisDto toRedisDto(UserDto userDto);

    UserDto toDto(UserRedisDto userRedisDto);
}
