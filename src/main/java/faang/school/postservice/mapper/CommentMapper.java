package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);

    @Mapping(target = "post", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "largeImageFileKey", ignore = true)
    @Mapping(target = "smallImageFileKey", ignore = true)
    Comment toEntity(CommentDto commentDto);

}
