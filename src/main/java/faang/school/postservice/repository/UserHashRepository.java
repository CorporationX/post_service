package faang.school.postservice.repository;

import faang.school.postservice.hash.UserHash;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHashRepository extends CrudRepository<UserHash, Long> {
    @Query()
    List<UserHash> findByIds(List<Long> userIds);

}
