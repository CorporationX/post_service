package faang.school.postservice.repository.redis.post;

import faang.school.postservice.model.redis.PostCache;

public interface PostCacheRepositoryCustom {
    boolean saveIfAbsent(PostCache postCache);
}
