package faang.school.postservice.redis.mapper;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.model.AuthorCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorCacheMapper {

    @Mapping(source = "postId", target = "postId")
    @Mapping(target = "subscribers", ignore = true)
    AuthorCache toAuthorCache(UserDto userDto, Long postId);
}
