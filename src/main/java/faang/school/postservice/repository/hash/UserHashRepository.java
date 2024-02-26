package faang.school.postservice.repository.hash;

import faang.school.postservice.dto.hash.UserHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHashRepository extends CrudRepository<UserHash, Long> {
}