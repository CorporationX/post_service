package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentCreateMapper {

    @Mapping(target = "post", ignore = true)
    Comment toEntity(CommentCreateDto commentCreateDto);
}
