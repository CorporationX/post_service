package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "likes", expression = "java(new ArrayList<>())")
    @Mapping(target = "comments", expression = "java(new ArrayList<>())")
    @Mapping(target = "published", expression = "java(false)")
    @Mapping(target = "deleted", expression = "java(false)")
    Post toEntity(PostDto postDto);

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "toLikeIds")
    @Mapping(source = "comments", target = "commentsIds", qualifiedByName = "toCommentsIds")
    PostDto toDto(Post post);

    List<PostDto> toDto(List<Post> posts);

    @Named("toLikeIds")
    default List<Long> toLikeIds(List<Like> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    @Named("toCommentsIds")
    default List<Long> toCommentsIds(List<Comment> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }
        return comments.stream()
                .map(Comment::getId)
                .toList();
    }

}