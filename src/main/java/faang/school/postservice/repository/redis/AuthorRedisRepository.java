package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.hash.AuthorHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRedisRepository extends CrudRepository<AuthorHash, Long> {
}
