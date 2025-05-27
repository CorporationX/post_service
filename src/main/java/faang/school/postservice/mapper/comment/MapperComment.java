package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
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
    @Mapping(target = "createData", source = "createdAt", dateFormat = "dd.MM.yyyy")
    CommentDtoResponse fromEntityToCreateDto(Comment comment);

    //todo доработать
    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "createData", source = "updatedAt", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "postId", source = "post")
    CommentDtoResponse fromEntityToModifiedDto(Comment comment);

    @Mapping(source = "postId", target = "post.id")
    Comment fromCreatDtoToEntity(CreateCommentDto createCommentDto);

    @Mapping(target = "id", source = "commentId")
    @Mapping(source = "postId", target = "post.id")
    Comment fromUpdateDtoToEntity(UpdateCommentDto updateCommentDto);

    default Long mapPostToId(Post post) {
        if (post == null) {
            return null;
        }
        return post.getId();
    }

    //todo доразобраться, дочитать статью
}
