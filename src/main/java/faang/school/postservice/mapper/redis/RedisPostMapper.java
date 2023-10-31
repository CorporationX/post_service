package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import org.mapstruct.*;
import org.springframework.data.relational.core.sql.In;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = RedisCommentMapper.class)
public interface RedisPostMapper {
    @Mapping(target = "postLikes", source = "likes", qualifiedByName = "mapLikesToRedisPost")
    @Mapping(source = "comments", target = "comments", qualifiedByName = "mapCommentsToRedisPost")
    RedisPost toRedisPost(Post post);

    @Mapping(target = "postLikes", source = "likes", qualifiedByName = "mapLikesDtoToRedisPost")
    RedisPost toRedisPost(PostDto postDto);
    Post toPost(RedisPost redisPost);

    @Named("mapLikesToRedisPost")
    default Integer mapLikesToDto(List<Like> likes) {
        return likes == null ? 0 : likes.size();
    }

    @Named("mapLikesDtoToRedisPost")
    default Integer mapLikesDtoToDto(List<LikeDto> likes) {
        return likes == null ? 0 : likes.size();
    }
}
