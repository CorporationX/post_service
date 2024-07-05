package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentKafkaEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.service.PostService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {PostService.class}
)
public interface CommentMapper {
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @Mapping(target = "post", source = "postId")
    Comment toEntity(CommentDto commentDto);

    @Mapping(source = "post.id", target = "postId")
    List<CommentDto> toDtoList(List<Comment> mentorshipEntity);

    CommentRedis fromKafkaEventToRedis(CommentKafkaEvent commentKafkaEvent);

    CommentKafkaEvent fromDtoToKafkaEvent(CommentDto commentDto);
}
