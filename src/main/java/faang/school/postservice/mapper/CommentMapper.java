package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentForCreationDto;
import faang.school.postservice.dto.comment.CommentForUpdateDto;
import faang.school.postservice.dto.comment.CommentOutputDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "likeIds", source = "likes")
//    @Mapping(target = "likeIds", source = "likes", qualifiedByName = "getLikeIds")
    CommentOutputDto toDto(Comment comment);

    @Mapping(target = "post", ignore = true)
    @Mapping(target = "likes", ignore = true)
    Comment toEntity(CommentForCreationDto commentDto);

    List<CommentOutputDto> toListDto(List<Comment> comments);

    default List<Long> mapLikesToIds(List<Like> likes) {
        return likes != null ? likes.stream().map(Like::getId).collect(Collectors.toList()) : null;
    }

//    @Named("getLikeIds")
//    default List<Long> getLikeIds(List<Like> likes) {
//        if (likes == null) {
//            return null;
//        }
//        return likes.stream().map(Like::getId).toList();
//    }

    Comment updateEntityFromDto(CommentForUpdateDto dto, @MappingTarget Comment entity);
}
