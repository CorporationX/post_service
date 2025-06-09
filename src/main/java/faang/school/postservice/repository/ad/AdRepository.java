package faang.school.postservice.repository.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.model.ad.AdStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdRepository extends CrudRepository<Ad, Long> {

    @Query("SELECT a FROM Ad a WHERE a.post.id = ?1")
    Optional<Ad> findByPostId(long postId);

    List<Ad> findAllByBuyerId(long buyerId);

    @Modifying
    @Query("UPDATE Ad a SET a.status = :newStatus " +
            "WHERE a.status = :currentStatus " +
            "AND (a.appearancesLeft = 0 OR a.endDate <= :now)")
    int updateExpiredAds(AdStatus newStatus, AdStatus currentStatus, LocalDateTime now);

    @Query("SELECT a.id FROM Ad a WHERE a.status = :status")
    Page<Long> findAdIdsByStatus(AdStatus status, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Ad a WHERE a.id IN :adIds")
    void deletePostAds(List<Long> adIds);
}
