package faang.school.postservice.repository.redis;

import faang.school.postservice.config.redis.entity.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {

    Optional<Author> findByAuthorId(Long authorId);
}