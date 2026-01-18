package faang.school.postservice.cache;

import faang.school.postservice.dto.cache.PostCacheDto;

import java.util.List;
import java.util.Map;

public interface PostCacheRepository {

    Map<Long, PostCacheDto> findAllByIds(List<Long> postIds);

    void saveAll(Map<Long, PostCacheDto> postsById);
}