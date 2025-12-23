package faang.school.postservice.repository.cache;

import faang.school.postservice.dto.author.AuthorDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorCacheRepository extends CrudRepository<AuthorDto, Long> {
}
