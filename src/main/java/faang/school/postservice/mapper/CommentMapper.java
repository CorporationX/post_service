package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.kafka.event.comment.CommentAddedEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.cache.model.CommentRedis;
import faang.school.postservice.cache.model.UserRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.TreeSet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(source = "likes", target = "likesId", qualifiedByName = "likesToLikesId")
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    Comment toEntity(CommentDto commentDto);

    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    CommentAddedEvent toCommentEvent(Comment comment);

    @Mapping(source = "commentId", target = "id")
    @Mapping(source = "authorId", target = "author", qualifiedByName = "authorIdToAuthor")
    CommentRedis toRedis(CommentAddedEvent event);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "authorId", target = "author", qualifiedByName = "authorIdToAuthor")
    CommentRedis toRedis(Comment comment);

    TreeSet<CommentRedis> toRedisTreeSet(List<Comment> comments);

    List<CommentRedis> toRedis(List<Comment> comments);

    @Named("likesToLikesId")
    default List<Long> likesToLikesId(List<Like> likes) {
        if (likes == null) return List.of();
        return likes.stream().map(Like::getId).toList();
    }

    @Named("authorIdToAuthor")
    default UserRedis authorIdToAuthor(Long authorId) {
        if (authorId == null) {
            return null;
        }
        return new UserRedis(authorId, null);
    }
}
