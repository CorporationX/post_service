package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentResponseMapper {

    @Mapping(target = "postId", expression = "java(getPostId(comment))")
    CommentResponseDto toDto(Comment comment);

    default long getPostId(Comment comment) {
        return comment.getPost().getId();
    }
}
