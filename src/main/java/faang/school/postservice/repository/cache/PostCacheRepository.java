package faang.school.postservice.repository.cache;

import faang.school.postservice.model.event.PostEvent;

public interface PostCacheRepository {

    void cachePost(PostEvent value);

    PostEvent getPost(Long id);

}
