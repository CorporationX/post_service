package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.event.AnalyticsCommentEvent;
import faang.school.postservice.model.event.NotificationCommentEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    List<Comment> toEntityList(List<CommentDto> commentDtos);

    List<CommentDto> toDtoList(List<Comment> comments);

    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    AnalyticsCommentEvent toAnalyticsCommentEvent(Comment comment);

    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "post.authorId", target = "postAuthorId")
    NotificationCommentEvent toNotificationCommentEvent(Comment comment);
}
