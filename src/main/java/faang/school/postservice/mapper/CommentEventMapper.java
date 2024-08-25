package faang.school.postservice.mapper;

import faang.school.postservice.dto.notification.CommentEvent;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEventMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "post.authorId", target = "authorPostId")
    CommentEvent toEvent(Comment comment);

    @Mapping(source = "postId", target = "post.id")
    @Mapping(source = "authorPostId", target = "post.authorId")
    Comment toEntity(CommentEvent commentEvent);
}
