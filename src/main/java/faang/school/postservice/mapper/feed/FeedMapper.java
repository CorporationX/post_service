package faang.school.postservice.mapper.feed;

import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.model.redis.PostRedis;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedMapper {

    PostFeedDto toPostFeedDto(PostRedis postRedis);
}
