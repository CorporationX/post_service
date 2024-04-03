package faang.school.postservice.mapper;

import faang.school.postservice.dto.event_broker.CommentEvent;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event_broker.CommentUserEvent;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEventMapper {

    @Mapping(target = "postId", source = "post.id")
    CommentEvent toEvent(Comment comment);

    CommentEvent toEvent(CommentDto commentDto);

    CommentEvent toEvent(CommentUserEvent commentUserEvent);

    CommentUserEvent toUserEvent(CommentEvent commentEvent);
}
