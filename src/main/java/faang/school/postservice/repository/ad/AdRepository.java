package faang.school.postservice.repository.ad;

import faang.school.postservice.entity.model.ad.Ad;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdRepository extends CrudRepository<Ad, Long> {

    @Query("SELECT a FROM Ad a WHERE a.post.id = ?1")
    Optional<Ad> findByPostId(long postId);

    List<Ad> findAllByBuyerId(long buyerId);

    @Query("SELECT a.id FROM Ad a WHERE a.endDate < CURRENT_TIMESTAMP OR a.appearancesLeft <= 0")
    List<Long> findAllExpiredIds();
}
