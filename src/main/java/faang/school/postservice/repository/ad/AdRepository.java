package faang.school.postservice.repository.ad;

import faang.school.postservice.model.ad.Ad;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query(nativeQuery = true, value = "DELETE FROM post_ad pa WHERE end_date <= now() or appearances_left = 0")
    @Modifying
    void deleteExpiredAds();

    @Query(nativeQuery = true, value = "SELECT * FROM post_ad pa ")
    List<Ad> findAll();

    @Query(nativeQuery = true, value = "DELETE FROM post_ad pa WHERE id = :id")
    @Modifying
    void deleteById(long id);

    @Query(nativeQuery = true, value = "DELETE FROM post_ad pa WHERE id in (:ids)")
    @Modifying
    void deleteByIds(List<Long> ids);
}
