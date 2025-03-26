package faang.school.postservice.mapper;

import faang.school.postservice.dto.tag.TagAddedToPostDto;
import faang.school.postservice.dto.tag.TagDto;
import faang.school.postservice.model.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagAddedToPostDto mapToTagAddedDto(Tag tag);

    TagDto mapToTagDto(Tag tag);
}
