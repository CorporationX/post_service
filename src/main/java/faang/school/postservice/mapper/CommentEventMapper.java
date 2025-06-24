package faang.school.postservice.mapper;

import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEventMapper {

    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "postId", source = "post.id")
    CommentEvent toEvent(Comment comment);
}