package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.image.CommentImageDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface CommentImageMapper {

    @Mapping(target = "largeObjectKey", source = "largeImageFileKey")
    @Mapping(target = "smallObjectKey", source = "smallImageFileKey")
    @Mapping(target = "postId", source = "post.id")
    CommentImageDto toDto(Comment comment);
}
