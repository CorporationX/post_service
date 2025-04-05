package faang.school.postservice.mapper.user;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserResponseDto;
import faang.school.postservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toUserDto(UserResponseDto userResponseDto);

    UserDto toUserDto(User user);

    User toUser(UserResponseDto userResponseDto);

    User toUser(UserDto userDto);
}
