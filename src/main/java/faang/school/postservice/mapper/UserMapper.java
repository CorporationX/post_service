package faang.school.postservice.mapper;

import faang.school.postservice.dto.cache.CacheUserDto;
import faang.school.postservice.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    CacheUserDto toCasheUserDto(UserDto userDto);
}
