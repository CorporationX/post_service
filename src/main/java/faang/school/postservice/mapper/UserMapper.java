package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.FeedUserDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.redis.UserCache;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface UserMapper {
    UserCache toUserCache(UserDto userDto);
    FeedUserDto toFeedUserDto(UserDto userDto);
    FeedUserDto toFeedUserDto(UserCache userCache);
}
