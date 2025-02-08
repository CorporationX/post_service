package faang.school.postservice.mapper.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.event.CommentEvent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEventMapper {

     CommentEvent toEvent(Comment comment);
}
