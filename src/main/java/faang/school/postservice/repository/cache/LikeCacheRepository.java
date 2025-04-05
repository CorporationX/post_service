package faang.school.postservice.repository.cache;

import faang.school.postservice.model.event.LikeEvent;

public interface LikeCacheRepository {

    void cacheLike(Long postId, LikeEvent likeEvent);
}
