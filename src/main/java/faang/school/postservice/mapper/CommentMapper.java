package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    CommentFeedDto toFeedDto(Comment comment);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "commentId", source = "id")
    CommentEventDto toEventDto(Comment comment);
}
