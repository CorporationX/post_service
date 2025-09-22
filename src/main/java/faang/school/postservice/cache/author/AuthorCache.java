package faang.school.postservice.cache.author;

import faang.school.postservice.dto.cache.AuthorCacheDto;

public interface AuthorCache {
    void put(AuthorCacheDto entry);
    AuthorCacheDto get(Long authorId);
    void delete(Long authorId);
}
