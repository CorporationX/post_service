package faang.school.postservice.cache.author;

import faang.school.postservice.dto.cache.AuthorCacheDto;

import java.util.List;

public interface AuthorCache {
    void put(AuthorCacheDto entry);
    void preloadAll(List<Long> authorIds);
    AuthorCacheDto get(Long authorId);
    void delete(Long authorId);
    List<AuthorCacheDto> getAll(List<Long> authorIds);
}
