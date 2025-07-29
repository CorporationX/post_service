package faang.school.postservice.mapper.redis;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.RedisPost;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisPostMapper {
    RedisPost toRedisPost(Post post);

    List<RedisPost> toRedisPosts(List<Post> posts);
}
