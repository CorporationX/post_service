package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SaveCommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.avro.post.CommentEvent;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentDto toCommentDto(Comment comment);

    CommentDto toCommentDto(CommentEvent comment);

    Comment toComment(SaveCommentDto saveCommentDto);

    List<CommentDto> toCommentDtos(List<Comment> comments);

    void update(SaveCommentDto saveCommentDto, @MappingTarget Comment comment);
}
