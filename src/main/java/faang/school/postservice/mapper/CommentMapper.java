package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "likeCount",
            expression = "java(comment.getLikes() != null ? comment.getLikes().size() : 0)")
    CommentDto toDto(Comment comment);
}
