package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentToCreateDto;
import faang.school.postservice.dto.comment.CommentToUpdateDto;
import faang.school.postservice.kafka.event.State;
import faang.school.postservice.kafka.event.comment.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.CommentLike;
import faang.school.postservice.redis.cache.entity.CommentCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentToCreateDto commentDto);

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @Mapping(source = "comment.likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    @Mapping(source = "comment.post.id", target = "postId")
    @Mapping(source = "comment.authorId", target = "userId")
    CommentEvent toKafkaEvent(Comment comment, State state);

    @Mapping(source = "userId", target = "author.id")
    CommentCache toRedisCache(CommentEvent comment);

    @Mapping(target = "id", ignore = true)
    void update(CommentToUpdateDto commentDto, @MappingTarget Comment comment);

    @Named("getCountFromLikeList")
    default long getCountFromLikeList(List<CommentLike> likes) {
        return likes != null ? likes.size() : 0;
    }

}
