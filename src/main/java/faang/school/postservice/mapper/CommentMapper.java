package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CommentMapper {
    CommentCreateDto toDto(Comment comment);

    Comment toEntity(CommentCreateDto commentCreateDto);

    Comment toEntity(CommentUpdateDto commentUpdateDto);
    Comment toEntity(CommentDtoResponse commentDtoResponse);

    Collection<CommentDtoResponse> toDtos(Collection<Comment> comments);
}
