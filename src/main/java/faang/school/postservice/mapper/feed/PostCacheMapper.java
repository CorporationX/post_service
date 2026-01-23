package faang.school.postservice.mapper.feed;

import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;

/**
 * Maps JPA entity -> lightweight cache DTO (no relations, no lazy fields).
 * Use this DTO for Redis "posts" cache entries.
 */
@Mapper(componentModel = "spring")
public interface PostCacheMapper {
    PostCacheDto toCacheDto(Post post);
}
