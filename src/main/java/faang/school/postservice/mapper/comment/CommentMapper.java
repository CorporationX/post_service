package faang.school.postservice.mapper.comment;

import faang.school.postservice.model.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "postId", target = "post.id")
    Comment mapToComment(CommentDto commentDto);

    @Mapping(source = "post.id", target = "postId")
    CommentDto mapToCommentDto(Comment comment);

    List<CommentDto> mapToCommentDto(List<Comment> comments);
}
