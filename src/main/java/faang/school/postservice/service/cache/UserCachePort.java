package faang.school.postservice.service.cache;

import faang.school.postservice.service.cache.model.UserCacheDto;

public interface UserCachePort {
    void put(UserCacheDto user);
    UserCacheDto get(Long userId);
    @SuppressWarnings("unused")
    void evict(Long userId);
}
