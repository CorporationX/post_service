package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDtoList(List<Comment> comments);

    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CreateCommentDto createCommentDto);

    @Mapping(target = "postId", source = "comment.post.id")
    @Mapping(target = "authorId", source = "comment.authorId")
    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "sendAt", expression = "java(java.time.LocalDateTime.now())")
    CommentEvent toEvent(Comment comment);
}
