package faang.school.postservice.mapper;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.hash.UserHash;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserHashMapper {
    UserHash toHash(UserDto userDto);
}
