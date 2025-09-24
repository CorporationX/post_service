package faang.school.postservice.mapper.user;

import faang.school.postservice.dto.cache.AuthorCacheDto;
import faang.school.postservice.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    AuthorCacheDto toCacheEntry(UserDto user);
    List<AuthorCacheDto> toCacheEntryList(List<UserDto> users);
}
