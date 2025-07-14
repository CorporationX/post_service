package faang.school.postservice.bjs_77879;

import faang.school.postservice.model.ad.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {

      List<Ad> findAllByEndDateBefore(LocalDateTime dateTime);
       void deleteByIds(List<Long> ids);

}