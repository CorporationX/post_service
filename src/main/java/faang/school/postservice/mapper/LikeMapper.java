package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    LikeDto toDto(Like like);

    @Mapping(source = "commentId", target = "comment")
    @Mapping(source = "postId", target = "post")
    Like toEntity(LikeDto likeDto);

    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    LikeEvent toEvent(Like like);

    default Comment mapIdToComment(Long id) {
        if (id == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setId(id);
        return comment;
    }

    default Post mapIdToPost(Long id) {
        if (id == null) {
            return null;
        }

        Post post = new Post();
        post.setId(id);
        return post;
    }
}
