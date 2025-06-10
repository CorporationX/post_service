package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.image.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "largeObjectKey", source = "largeImageFileKey")
    @Mapping(target = "smallObjectKey", source = "smallImageFileKey")
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDto(Comment comment);
}
