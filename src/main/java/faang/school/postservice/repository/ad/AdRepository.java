package faang.school.postservice.repository.ad;

import faang.school.postservice.model.ad.Ad;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdRepository extends CrudRepository<Ad, Long> {

    @Query("SELECT a FROM Ad a WHERE a.post.id = ?1")
    Optional<Ad> findByPostId(long postId);

    List<Ad> findAllByBuyerId(long buyerId);

    @Query("SELECT a FROM Ad a WHERE a.post.id = ?1")
    List<Long> getExpiredAdIds(LocalDateTime localDateTime);

    @Query("SELECT a.id FROM Ad a WHERE a.appearances_left = 0 OR a.endDate < CURRENT_TIMESTAMP")
    List<Long> findExpiredAdIds();
}
