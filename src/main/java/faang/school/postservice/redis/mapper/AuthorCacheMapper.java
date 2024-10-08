package faang.school.postservice.redis.mapper;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.model.AuthorCache;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorCacheMapper {
    AuthorCache toAuthorCache(UserDto userDto);
}
