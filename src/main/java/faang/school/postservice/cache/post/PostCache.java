package faang.school.postservice.cache.post;

import faang.school.postservice.dto.cache.PostCacheDto;

import java.util.List;
import java.util.Optional;

public interface PostCache {
    void put(PostCacheDto entry);
    Optional<PostCacheDto> get(Long postId);
    List<PostCacheDto> getAll(List<Long> ids);
    void delete(Long postId);
    void putAll(List<PostCacheDto> posts);
}