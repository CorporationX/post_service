package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "post", ignore = true)
    Comment toEntity(CommentDto dto);
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment entity);
}
