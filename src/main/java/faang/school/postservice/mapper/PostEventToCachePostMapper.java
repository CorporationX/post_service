package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.model.cache.CachePost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostEventToCachePostMapper {

    @Mapping(target = "id", expression = "java(String.valueOf(postEvent.id()))")
    CachePost toCachePost(PostEvent postEvent);
}
