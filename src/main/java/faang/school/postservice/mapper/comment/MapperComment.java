package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapperComment {

    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "postId", source = "post")
    @Mapping(target = "createData", source = "createdAt", dateFormat = "dd.MM.yyyy HH:mm:ss")
    @Mapping(target = "updateData", source = "updatedAt", dateFormat = "dd.MM.yyyy HH:mm:ss")
    CommentDtoResponse fromEntityToDto(Comment comment);

    @Mapping(source = "postId", target = "post.id")
    Comment fromCreatDtoToEntity(CommentCreateDto dto);

    @Mapping(target = "id", source = "commentId")
    Comment fromUpdateDtoToEntity(CommentUpdateDto dto);

    default Long mapPostToId(Post post) {
        if (post == null) {
            return null;
        }
        return post.getId();
    }
}
