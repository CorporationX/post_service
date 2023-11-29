package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.cash.UserCache;
import faang.school.postservice.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserCacheMapper {

    @Mapping(target = "id", expression = "java(String.valueOf(user.getId()))")
    UserCache toCacheDto(UserDto user);

    @Mapping(target = "id", expression = "java(Long.valueOf(userCache.getId()))")
    UserDto toCacheEntity(UserCache userCache);
}
