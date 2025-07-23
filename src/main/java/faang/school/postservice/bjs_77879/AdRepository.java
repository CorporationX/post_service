package faang.school.postservice.bjs_77879;

import faang.school.postservice.model.ad.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface AdRepository extends JpaRepository<Ad, Long> {

     @Modifying
     @Transactional
     @Query
     int deleteAllByEndDateBefore(LocalDateTime dateTime);
}