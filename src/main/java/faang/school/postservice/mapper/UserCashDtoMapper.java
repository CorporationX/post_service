package faang.school.postservice.mapper;

import faang.school.postservice.dto.user.UserCashDto;
import faang.school.postservice.dto.user.UserDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserCashDtoMapper {
    @Mapping(target = "ttl", ignore = true)
    UserCashDto toUserCashDto(UserDto userDto, @Context Long ttl);

    UserDto toUserDto(UserCashDto userDto);

    @AfterMapping
    default void setTtl(@MappingTarget UserCashDto dto, @Context Long ttl) {
        dto.setTtl(ttl);
    }
}
