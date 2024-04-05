package faang.school.postservice.repository;

import faang.school.postservice.hash.UserHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHashRepository extends CrudRepository<UserHash, Long> {

}
