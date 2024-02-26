package faang.school.postservice.repository;

import faang.school.postservice.dto.post.FeedHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedHashRepository extends CrudRepository<FeedHash, Long> {
}