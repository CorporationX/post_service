package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import org.mapstruct.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = RedisCommentMapper.class)
public interface RedisPostMapper {
    @Mapping(target = "postLikes", source = "likes", qualifiedByName = "mapLikesToRedisPost")
    @Mapping(source = "comments", target = "comments", qualifiedByName = "mapCommentsToRedisPost")
    RedisPost toRedisPost(Post post);

    @Mapping(target = "postLikes", source = "likes", qualifiedByName = "mapLikesDtoToRedisPost")
    RedisPost toRedisPost(PostDto postDto);

    @Named("mapLikesToRedisPost")
    default AtomicInteger mapLikesToRedisPost(List<Like> likes) {
        return new AtomicInteger(likes == null ? 0 : likes.size());
    }

    @Named("mapLikesDtoToRedisPost")
    default AtomicInteger mapLikesDtoToDto(List<LikeDto> likes) {
        return new AtomicInteger(likes == null ? 0 : likes.size());
    }
}
