package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    //for posts
    @Mapping(target = "post", source = "postId", qualifiedByName = "toPost")
    Like dtoToLike(LikeDto likeDto);

    @Mapping(target = "postId", source = "post", qualifiedByName = "toPostId")
    LikeDto likeToDto(Like like);

    //for comments
    @Mapping(target = "comment", source = "commentId", qualifiedByName = "toComment")
    Like dtoToEntity(LikeDto likeDto);

    @Mapping(target = "commentId", source = "comment", qualifiedByName = "toCommentId")
    LikeDto entityToDto(Like like);

    @Named(value = "toPost")
    default Post toPost(Long postId) {
        return Post.builder().id(postId).build();
    }

    @Named(value = "toComment")
    default Comment toComment(Long commentId) {
        return Comment.builder().id(commentId).build();
    }

    @Named(value = "toPostId")
    default Long toPostId(Post post) {
        return post.getId();
    }

    @Named(value = "toCommentId")
    default Long toCommentId(Comment comment) {
        return comment.getId();
    }
}
