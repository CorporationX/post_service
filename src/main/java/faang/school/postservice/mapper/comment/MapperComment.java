package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapperComment {

    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "postId", source = "post")
    @Mapping(target = "createData", source = "createdAt", dateFormat = "dd.MM.yyyy HH:mm:ss")
    @Mapping(target = "updateData", source = "updatedAt", dateFormat = "dd.MM.yyyy HH:mm:ss")
    CommentDtoResponse fromEntityToDto(Comment comment);

    List<CommentDtoResponse> fromDtoListToEntityList(List<Comment> comments);

    default Long mapPostToId(Post post) {
        if (post == null) {
            return null;
        }
        return post.getId();
    }
}
