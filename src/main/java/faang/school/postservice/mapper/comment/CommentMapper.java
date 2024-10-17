package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedCommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikesToLikeIds")
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @Mapping(source = "likeIds", target = "likes", qualifiedByName = "mapLikeIdsToLike")
    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentDto commentDto);

    CommentDto fromFeedCommentEventToDto(FeedCommentEvent event);

    @Named("mapLikesToLikeIds")
    default List<Long> mapLikesToLikeIds(List<Like> likes) {
        if (likes == null) {
            return null;
        }

        return likes.stream()
                .map(Like::getId)
                .collect(Collectors.toList());
    }

    @Named("mapLikeIdsToLike")
    default List<Like> mapLikeIdsToLike(List<Long> likeIds) {
        if (likeIds == null) {
            return null;
        }

        return likeIds.stream()
                .map(id -> {
                    Like like = new Like();
                    like.setId(id);
                    return like;
                })
                .collect(Collectors.toList());
    }
}
