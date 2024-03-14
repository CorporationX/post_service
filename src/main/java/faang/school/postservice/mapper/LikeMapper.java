package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "commentId", source = "comment.id")
    LikeDto toDto(Like entity);

    @Mapping(target = "post.id", source = "postId")
    @Mapping(target = "comment", source = "commentId", qualifiedByName = "mapComment")
    Like toEntity(LikeDto dto);

    @Named("mapComment")
    default Comment mapComment(Long commentId) {
        if (commentId == null) {
            return null;
        }
        return Comment.builder().id(commentId).build();
    }
}
