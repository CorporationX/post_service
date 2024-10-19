package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.notification.CommentEvent;
import faang.school.postservice.event.comment.CommentKafkaEvent;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {


    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentDto commentDto);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "post.authorId", target = "authorPostId")
    CommentEvent toEvent(Comment comment);

    CommentKafkaEvent toKafkaEvent(CommentDto commentDto);
}
