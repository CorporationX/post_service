package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.event.kafka.KafkaCommentEvent;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentDto entityToDto(Comment comment);

    @Mapping(source = "postId", target = "post.id")
    Comment dtoToEntity(CommentDto commentDto);

    @Mapping(source = "post.id", target = "postId")
    KafkaCommentEvent toKafkaEvent(Comment comment);

    @Mapping(source = "post.id", target = "postId")
    List<CommentDto> entitiesToDtos(List<Comment> projectList);
}
