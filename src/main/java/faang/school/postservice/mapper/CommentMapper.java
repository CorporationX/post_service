package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
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

    @Mapping(source = "dto.content", target = "content")
    @Mapping(source = "dto.authorId", target = "authorId")
    @Mapping(source = "post", target = "post")
    Comment toEntity(CreateCommentDto dto, Post post);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "postAuthorId", source = "post.authorId")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "commentContent", source = "content")
    @Mapping(target = "sendAt", expression = "java(java.time.LocalDateTime.now())")
    CommentEvent toEvent(Comment comment);
}
