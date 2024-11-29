package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.redis.CachedAuthor;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CachedAuthorMapper {
    CachedAuthor toCachedAuthor(UserDto userDto);
}
