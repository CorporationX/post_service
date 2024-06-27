package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostDto postDto);

    @Mapping(source = "likes", target = "likesIds", qualifiedByName = "likesToLikesIds")
    @Mapping(source = "comments", target = "commentsIds", qualifiedByName = "commentsToCommentsIds")
    PostDto toDto(Post post);

    List<PostDto> toDto(List<Post> posts);

    @Named("likesToLikesIds")
    default List<Long> likesToLikesIds(List<Like> likes) {
        if (likes == null) {
            return null;
        }

        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    @Named("commentsToCommentsIds")
    default List<Long> commentsToCommentsIds(List<Comment> comments) {
        if (comments == null) {
            return null;
        }

        return comments.stream()
                .map(Comment::getId)
                .toList();
    }
}
