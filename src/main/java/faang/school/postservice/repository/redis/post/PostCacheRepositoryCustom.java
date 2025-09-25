package faang.school.postservice.repository.redis.post;

import faang.school.postservice.model.redis.PostCache;

import java.util.List;

public interface PostCacheRepositoryCustom {
    List<PostCache> getMany(List<Long> ids);
}
