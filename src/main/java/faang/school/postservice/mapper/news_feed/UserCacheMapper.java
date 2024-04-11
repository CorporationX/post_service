package faang.school.postservice.mapper.news_feed;

import faang.school.postservice.dto.UserDto;
import faang.school.postservice.model.redis.UserCache;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserCacheMapper {

    UserDto toDto(UserCache userCache);
}
