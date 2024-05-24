package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public interface CommentMapper {

    Comment dtoToComment(CommentDto commentDto);

    CommentDto commentToDto(Comment comment);

    List<CommentDto> commentsToCommentsDto(List<Comment> comments);
}
