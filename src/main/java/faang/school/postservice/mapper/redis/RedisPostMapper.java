package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisPostMapper {

    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikesToDto")
    RedisPost toRedisPost(Post post);

    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikesDtoToDto")
    RedisPost toRedisPost(PostDto post);

    @Named("mapLikesToDto")
    default Integer mapLikesToDto(List<Like> likes) {
        return likes.size();
    }

    @Named("mapLikesDtoToDto")
    default Integer mapLikesDtoToDto(List<LikeDto> likes) {
        return likes.size();
    }
}
