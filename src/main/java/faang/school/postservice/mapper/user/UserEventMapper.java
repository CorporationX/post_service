package faang.school.postservice.mapper.user;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.event.UserEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ResourceMapper.class)
public interface UserEventMapper {


    @Mapping(target = "followeesIds", source = "followees")
    @Mapping(target = "ttl", ignore = true)
    UserEvent toEvent(UserDto userDto);

    default List<Long> mapFollowees(List<UserDto> followees) {
        return followees != null
                ? followees.stream()
                .map(UserDto::getId)
                .collect(Collectors.toList())
                : null;
    }
}
