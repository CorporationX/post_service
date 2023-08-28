package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    LikeMapper INSTANCE = Mappers.getMapper(LikeMapper.class);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "commentId", source = "comment.id")
    LikeDto toDto(Like entity);

    @Mapping(target = "post", source = "postId", qualifiedByName = "mapPost")
    @Mapping(target = "comment", source = "commentId", qualifiedByName = "mapComment")
    Like toEntity(LikeDto dto);

    @Named("mapComment")
    default Comment mapComment(Long commentId) {
        if (commentId == null) {
            return null;
        }
        return Comment.builder().id(commentId).build();
    }

    @Named("mapPost")
    default Post mapPost(Long postId) {
        if (postId == null) {
            return null;
        }
        return Post.builder().id(postId).build();
    }
}
