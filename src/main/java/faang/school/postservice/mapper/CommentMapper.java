package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentCreateUpdateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentCreateUpdateDto commentCreateDto);

    List<CommentDto> toCommentDtos(List<Comment> comments);
}
