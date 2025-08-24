package faang.school.postservice.mapper.comment;

import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.event.comment.CommentEvent;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface CommentEventMapper {
    @Mapping(source = "post.id", target = "postId")
    CommentEvent toCommentEvent(Comment comment);
}
