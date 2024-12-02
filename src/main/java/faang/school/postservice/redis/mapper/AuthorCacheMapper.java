package faang.school.postservice.redis.mapper;

import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.redis.model.dto.AuthorRedisDto;
import faang.school.postservice.redis.model.entity.AuthorCache;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorCacheMapper {

    AuthorCache toAuthorCache(UserDto userDto);
    AuthorRedisDto toAuthorRedisDto(AuthorCache authorCache);
    List<AuthorRedisDto> toAuthorRedisDtos(List<UserDto> userDtos);
}
