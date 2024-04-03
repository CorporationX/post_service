package faang.school.postservice.repository.hash;

import faang.school.postservice.dto.hash.FeedHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedHashRepository extends CrudRepository<FeedHash, Long> {
}