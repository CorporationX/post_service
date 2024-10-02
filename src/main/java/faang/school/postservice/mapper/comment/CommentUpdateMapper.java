package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentUpdateMapper {
    Comment toEntity(CommentUpdateDto commentUpdateDto);

    CommentUpdateDto toDto(Comment comment);
}
