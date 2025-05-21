package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentDto dto);
    CommentDto toDto(Comment entity);
}
