package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentRequestMapper {
    @Mapping(target = "post", ignore = true)
    Comment toComment(CommentRequestDto commentRequestDto);

    @Mapping(target = "postId", source = "post.id")
    CommentRequestDto toCommentDto(Comment comment);
}

