package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.hash.AuthorHash;
import faang.school.postservice.dto.hash.AuthorType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRedisRepository extends CrudRepository<AuthorHash, Long> {
    void saveInRedis(AuthorHash authorHash);

    Optional<AuthorHash> findByIdAndAuthorType(Long userId, AuthorType authorType);
}
