package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentResponseMapper {
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "likes", ignore = true)
    Comment toComment(CommentResponseDto commentDto);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "likeCount", expression = "java(comment.getLikes().size())")
    CommentResponseDto toCommentDto(Comment comment);
}
