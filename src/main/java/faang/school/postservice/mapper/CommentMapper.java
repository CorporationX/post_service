package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    Comment toEntity(CommentDto commentDto);

    CommentDto toDto(Comment comment);

    @Mapping(target = "id", source = "commentId")
    @Mapping(target = "content", source = "commentText")
    @Mapping(target = "authorId", source = "commentAuthorId")
    CommentDto toDto(CommentEventDto commentEventDto);

    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "commentAuthorId", source = "authorId")
    @Mapping(target = "postAuthorId", source = "post.authorId")
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "commentText", source = "content")
    CommentEventDto toEventDto(Comment comment);

    List<CommentDto> toDto(List<Comment> comments);
}
