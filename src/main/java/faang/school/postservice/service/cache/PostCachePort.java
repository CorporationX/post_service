package faang.school.postservice.service.cache;
import faang.school.postservice.service.cache.model.PostCacheDto;

public interface PostCachePort {
    void put(PostCacheDto post);
    PostCacheDto get(Long postId);
    @SuppressWarnings("unused")
    void evict(Long postId);
}
