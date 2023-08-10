package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    LikeDto toDto(Like like);

    @Mapping(source = "postId", target = "Post", qualifiedByName = "getPost")
    @Mapping(source = "commentId", target = "CommentId", qualifiedByName = "getComment")
    Like toEntity(LikeDto likeDto);

    @Named("getPost")
    default Post getPost(long postId){
        Post post = new Post();
        post.setId(postId);
        return post;
    }

    @Named("getComment")
    default Comment getComment(long commentId){
        Comment comment = new Comment();
        comment.setId(commentId);
        return comment;
    }
}
