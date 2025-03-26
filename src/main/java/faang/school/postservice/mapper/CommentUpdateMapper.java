package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentUpdateMapper {
    Comment toComment(CommentUpdateDto commentUpdateDto);

    CommentUpdateDto toCommentDto(Comment comment);
}

