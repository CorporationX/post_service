package faang.school.postservice.repository;

import faang.school.postservice.model.AuthorCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorCacheRedisRepository extends CrudRepository<AuthorCache, Long> {}
