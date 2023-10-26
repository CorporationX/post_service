package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RedisPostMapper {
    @Mapping(source = "comments", target = "comments", qualifiedByName = "mapCommentsToRedis")
    RedisPost toRedisPost(Post post);

    RedisPost toRedisPost(PostDto postDto);

    Post toPost(RedisPost redisPost);

//    @Named("mapCommentsToRedis")
//    default List<RedisPost> mapCommentsToRedis(List<RedisCommentDto> comments) {
//
//    }
}
