package faang.school.postservice.mapper;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.cache.entity.AuthorRedisCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorMapper {

    @Mapping(source = "userProfilePic.fileId", target = "smallFileId")
    AuthorRedisCache toAuthorCache(UserDto userDto);

    @Mapping(source = "smallFileId", target = "userProfilePic.fileId")
    UserDto fromAuthorCache(AuthorRedisCache authorRedisCache);
}
