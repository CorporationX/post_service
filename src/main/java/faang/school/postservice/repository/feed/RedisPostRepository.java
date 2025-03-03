package faang.school.postservice.repository.feed;

import faang.school.postservice.model.feed.PostCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<PostCache, String> {
}
