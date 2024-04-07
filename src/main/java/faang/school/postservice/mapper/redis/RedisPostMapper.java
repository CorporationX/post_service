package faang.school.postservice.mapper.redis;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisComment;
import faang.school.postservice.model.redis.RedisPost;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RedisPostMapper {

    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapPostLikes")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "mapPostComments")
    RedisPost toRedisEntity(Post post);

    @Named("mapPostLikes")
    default long mapPostLikes(List<Like> likes){
        return (long) likes.size();
    }

    @Named("mapPostComments")
    default Queue<RedisComment> mapPostComments(List<Comment> comments){
        RedisCommentMapper redisCommentMapper = new RedisCommentMapperImpl();
        return comments.stream()
                .map(comment -> redisCommentMapper.toRedisEntity(comment))
                .sorted()
                .limit(3)
                .collect(Collectors.toCollection(() -> new PriorityQueue<RedisComment>()));
    }

}
