package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentToCreateDto;
import faang.school.postservice.dto.comment.CommentToUpdateDto;
import faang.school.postservice.event.kafka.CommentKafkaEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.redis.AuthorRedisCache;
import faang.school.postservice.model.redis.CommentRedisCache;
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

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "authorId", target = "userId")
    CommentKafkaEvent toKafkaEvent(Comment comment);

    @Mapping(source = "authorId", target = "id")
    AuthorRedisCache toAuthorCache(Comment comment);

    @Mapping(source = "userId", target = "author.id")
    CommentRedisCache toRedisCache(CommentKafkaEvent comment);

    @Mapping(target = "id", ignore = true)
    void update(CommentToUpdateDto commentDto, @MappingTarget Comment comment);

    @Named("getCountFromLikeList")
    default int getCountFromLikeList(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }
}
