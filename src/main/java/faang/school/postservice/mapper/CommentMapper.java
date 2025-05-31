package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.feed.CommentRedisEvent;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDto toDto(Comment comment);

    Comment toEntity(CommentDto commentDto);

    List<CommentDto> toDtoList(List<Comment> comments);

    CommentDto eventToDto(CommentRedisEvent event);
}
