package faang.school.postservice.mapper;

import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.redis.UserRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "ownerType", constant = "USER")
    UserRedis toUserRedis(UserDto dto);

    @Mapping(source = "title", target = "username")
    @Mapping(target = "ownerType", constant = "PROJECT")
    UserRedis toUserRedis(ProjectDto dto);
}
