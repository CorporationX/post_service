package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.event.comment.CommentsEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "likes", target = "likesId", qualifiedByName = "listLikesToLong")
    CommentDto toDto(Comment comment);

    @Mapping(source = "likesId", target = "likes", qualifiedByName = "listLongToLikes")
    Comment toEntity(CommentDto commentDto);

    @Named("listLikesToLong")
    default List<Long> listLikesToLong(List<Like> likes){
        return likes.stream().map(Like::getId).toList();
    }

    @Named("listLongToLikes")
    default List<Like> listLongToLikes(List<Long> likes) {
        return likes.stream().map(likeId -> Like.builder().id(likeId).build()).toList();
    }

    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    CommentEvent toEvent(Comment comment);

    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    CommentsEvent toCommentsEvent(Comment comment);
}