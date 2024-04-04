package faang.school.postservice.repository;

import faang.school.postservice.hash.FeedHash;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedHashRepository extends CrudRepository<FeedHash, Long> {
    @Query("SELECT fh FROM FeedHash fh WHERE fh.userId = :userId")
    Optional<FeedHash> findByUserId(Long userId);
}
