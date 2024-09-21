package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(source = "likeIds", target = "likes", qualifiedByName = "mapToLikes")
    @Mapping(source = "commentIds", target = "comments", qualifiedByName = "mapToComments")
    Post toPost(PostDto postDto);

    @Named("mapToLikes")
    default List<Like> mapToLikes(List<Long> likeIds) {
        if (likeIds == null) {
            return null;
        }

        return likeIds.stream()
                .map(id -> Like.builder().id(id).build())
                .toList();
    }

    @Named("mapToComments")
    default List<Comment> mapToComments(List<Long> commentIds) {
        if (commentIds == null) {
            return null;
        }

        return commentIds.stream()
                .map(id -> Comment.builder().id(id).build())
                .toList();
    }


    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapToLikeIds")
    @Mapping(source = "comments", target = "commentIds", qualifiedByName = "mapToCommentIds")
    PostDto toPostDto(Post post);

    List<PostDto> toPostDtos(List<Post> post);

    @Named("mapToLikeIds")
    default List<Long> mapToLikeIds(List<Like> likes) {
        if (likes == null) {
            return null;
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    @Named("mapToCommentIds")
    default List<Long> mapToCommentIds(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(Comment::getId)
                .toList();
    }
}
