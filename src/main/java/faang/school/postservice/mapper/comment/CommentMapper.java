package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.event.kafka.CommentKafkaEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.service.post.PostService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {PostService.class})
public interface CommentMapper {
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "likes", target = "likesId", qualifiedByName = "listLikesToLong")
    CommentDto toDto(Comment comment);

    @Mapping(source = "likesId", target = "likes", qualifiedByName = "listLongToLikes")
    Comment toEntity(CommentDto commentDto);

    @Named("listLikesToLong")
    default List<Long> listLikesToLong(List<Like> likes) {
        return likes.stream().map(Like::getId).toList();
    }

    @Named("listLongToLikes")
    default List<Like> listLongToLikes(List<Long> likes) {
        return likes.stream().map(likeId -> Like.builder().id(likeId).build()).toList();
    }

    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    CommentEvent toEvent(Comment comment);
    CommentRedis commentToCommentRedis(Comment comment);

    CommentRedis toCommentRedis(CommentKafkaEvent comment);

    CommentKafkaEvent toCommentKafkaEvent(CommentDto commentDto);

    @Named("mapComments")
    default TreeSet<CommentRedis> mapComments(List<Comment> comments){
        return comments.stream()
                .map(this::commentToCommentRedis)
                .limit(3)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
