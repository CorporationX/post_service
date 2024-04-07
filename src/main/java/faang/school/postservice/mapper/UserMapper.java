package faang.school.postservice.mapper;

import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.redis.UserRedisDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "name", source = "username")
    UserRedisDto toUserRedisDto(UserDto userDto);
}
