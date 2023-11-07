package faang.school.postservice.repository;

import faang.school.postservice.model.RedisFeed;
import org.springframework.data.repository.CrudRepository;

public interface RedisFeedRepository extends CrudRepository<RedisFeed, Long> {
}
