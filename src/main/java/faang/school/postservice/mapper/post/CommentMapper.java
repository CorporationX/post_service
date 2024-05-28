package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    Comment fromDto(CommentDto commentDto);
}
