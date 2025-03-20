package faang.school.postservice.mapper;

import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.dto.tag.TagSearchDto;
import faang.school.postservice.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagSearchDto mapToTagSearchDto(Tag tag);

    @Mapping(target = "creatorId", source = "userId")
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Tag toEntity(TagDto tagDto, Long userId);

    TagDto mapToTagDto(Tag tag);
}
