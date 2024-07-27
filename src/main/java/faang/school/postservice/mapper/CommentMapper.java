package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "likeIds", target = "likes", qualifiedByName = "mapToLikes")
    @Mapping(source = "postId", target = "post", qualifiedByName = "mapToPost")
    Comment toEntity(CommentDto dto);

    List<Comment> toEntity(List<CommentDto> dtos);

    @Named("mapToLikes")
    default List<Like> mapToLikes(List<Long> likeIds) {
        if (likeIds == null) {
            return null;
        }

        return likeIds.stream()
                .map(likeId -> Like.builder()
                        .id(likeId)
                        .build()
                )
                .toList();
    }

    @Named("mapToPost")
    default Post mapToPost(Long postId) {
        if (postId == null) {
            return null;
        }
        return Post.builder().id(postId).build();
    }

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapToLikeIds")
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment entity);

    List<CommentDto> toDto(List<Comment> entities);

    @Named("mapToLikeIds")
    default List<Long> mapToLikeIds(List<Like> likes) {
        if (likes == null) {
            return null;
        }

        return likes.stream()
                .map(Like::getId)
                .toList();
    }
}
