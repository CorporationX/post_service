package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    Comment fromDto(CommentDto commentDto);

    @Named("toCommentEvent")
    default CommentEvent toCommentEvent(Comment comment) {
        return CommentEvent.builder()
                .commentAuthorId(comment.getAuthorId())
                .postAuthorId(comment.getPost().getAuthorId())
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .build();
    }
}
