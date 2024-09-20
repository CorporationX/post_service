package faang.school.postservice;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(source = "likeId", target = "id")
    @Mapping(source = "postId", target = "post", qualifiedByName = "convertIdToPost")
    @Mapping(source = "commentId", target = "comment", qualifiedByName = "convertIdToComment")
    Like toEntity(LikeDto likeDto);

    @Mapping(source = "id", target = "likeId")
    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    LikeDto toDto(Like like);

    @Named("convertIdToPost")
    default Post convertIdToPost(Long id) {
        Post post = new Post();
        post.setId(id);
        return post;
    }

    @Named("convertIdToComment")
    default Comment convertIdToComment(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        return comment;
    }
}
