package faang.school.postservice.mapper.redis;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RedisPostMapper {

    RedisPost toDto(Post post);
}
