package faang.school.postservice.mapper.redis;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisComment;
import faang.school.postservice.model.redis.RedisPost;
import org.mapstruct.*;

import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collector;
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
    default PriorityQueue<RedisComment> mapPostComments(List<Comment> comments){
//        RedisCommentMapper redisCommentMapper = new RedisCommentMapperImpl();
//        return comments.stream()
//                .map(comment -> redisCommentMapper.toRedisEntity(comment))
//                .sorted((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()))
//                .limit(3)
//                .collect(Collectors.toCollection(() -> new PriorityQueue<RedisComment>()));
        return  new PriorityQueue<>();
    }
}
