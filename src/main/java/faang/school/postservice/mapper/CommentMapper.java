package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.dto.kafka_events.CommentKafkaEvent;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(target = "post.id", source = "postId")
    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    CommentDto toDto(CommentKafkaEvent commentKafkaEvent);

    List<CommentDto> toDtoList(List<Comment> comments);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "receiverId", source = "post.authorId")
    @Mapping(target = "commentId", source = "id")
    CommentEventDto toEventDto(Comment comment);
}
