package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.PostPair;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = RedisCommentMapper.class)
public interface RedisPostMapper {

    @Mapping(target = "postId", source = "id")
    @Mapping(target = "postLikes", source = "likes", qualifiedByName = "countAmountOfLikes")
    @Mapping(target = "commentsDto", source = "comments", qualifiedByName = "mapCommentsToRedisCommentDto")
    @Mapping(target = "postViews", source = "views")
    RedisPost toRedisPost(Post post);

    RedisPostDto toRedisPostDto(RedisPost post);

    PostPair toPostPair(RedisPost redisPost);

}
