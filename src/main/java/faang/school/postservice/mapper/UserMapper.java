package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.UserFeedDto;
import faang.school.postservice.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserFeedDto toFeedDto(UserDto user);
}
