package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    @Mapping(target = "post.id", source = "postId")
    Comment toEntity(CommentDto dto);

    List<CommentDto> toDto(List<Comment> comments);
}