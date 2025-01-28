package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "likes", target = "likeIds")
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @Mapping(source = "likes", target = "likeIds")
    @Mapping(source = "post.id", target = "postId")
    CommentEvent toEvent(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "likes", ignore = true)
    void update(CommentDto commentDto, @MappingTarget Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    Comment toEntity(CommentDto commentDto);

    List<CommentDto> toDto(List<Comment> comments);
    default List<Long> mapLikesToIds(List<Like> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    List<Comment> toEntity(List<CommentDto> commentsDto);
    default List<Like> mapIdsToLikes(List<Long> likeIds) {
        if (likeIds == null) {
            return new ArrayList<>();
        }
        return likeIds.stream()
                .map(id -> {
                    Like like = new Like();
                    like.setId(id);
                    return like;
                })
                .toList();
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