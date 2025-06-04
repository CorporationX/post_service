package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "post.id", source = "postId")
    Comment toEntity(CommentDto commentDto);
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);
}
