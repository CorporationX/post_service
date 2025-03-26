package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CommentMapper {
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "likes", ignore = true)
    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "likeCount", expression = "java(comment.getLikes().size())")
    CommentDto toDto(Comment comment);
}
