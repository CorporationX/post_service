package faang.school.postservice.mapper.author;

import faang.school.postservice.cache.model.author.AuthorCache;
import faang.school.postservice.dto.author.AuthorDto;
import faang.school.postservice.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorMapper {

    AuthorDto toAuthorDto(AuthorCache authorCache);

    @Mapping(target = "userId", source = "id")
    AuthorCache toAuthorCache(UserDto userDto);
}