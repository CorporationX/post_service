package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentPublishedEvent;
import faang.school.postservice.mapper.DateTimeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.protobuf.generate.CommentPublishedEventProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


/**
 * @Todo: необходимо в дальнейшем пересмотреть логику перевода времени, потому что мы используем здесь постоянный часовой пояс
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentPublishedEventMapper extends DateTimeMapper {
    CommentPublishedEventProto.CommentPublishedEvent toProto(CommentPublishedEvent commentEvent);

    CommentPublishedEvent toEvent(CommentPublishedEventProto.CommentPublishedEvent proto);

    @Mapping(target = "postId", source = "post.id")
    CommentPublishedEvent fromCommentToEvent(Comment comment);
}
