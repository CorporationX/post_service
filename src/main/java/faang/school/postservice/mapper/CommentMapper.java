package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.event.comment.NewCommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(source = "post.id", target = "postId")
    CommentDto commentToDto(Comment comment);
    @Mapping(source = "postId", target = "post.id")
    Comment commentToEntity(CommentDto commentDto);

    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "authorCommentId", source = "authorId")
    @Mapping(target = "authorPostId", source = "post.authorId")
    NewCommentEvent entityToEventType(Comment commentDto);
}
