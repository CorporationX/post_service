package faang.school.postservice.redis.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.model.PostCache;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PostEventMapper {

    PostCache toPostCache(PostDto postDto);
}
