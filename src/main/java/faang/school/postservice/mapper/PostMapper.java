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

        @Mapping(target = "likeIds", source = "likes", qualifiedByName = "getLikeIds")
        @Mapping(target = "commentIds", source = "comments", qualifiedByName = "getCommentIds")
        PostDto toDto(Post post);

        @Mapping(target = "likes", ignore = true)
        @Mapping(target = "comment", ignore = true)
        //Post toEntity(PostDto postDto);


        @Named("getLikeIds")
        default List<Long> getLikeIds(List<Like> likes) {
            if (likes == null) {
                return null;
            }
            return likes.stream().map(Like::getId).toList();
        }

    @Named("getCommentIds")
    default List<Long> getCommentIds(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream().map(Comment::getId).toList();
    }
    }

