package faang.school.postservice.bjs_77879;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j
public class AdService {
    private final AdRepository adRepository;
    public AdService(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    public void deleteExpiredAds() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = adRepository.deleteAllByEndDateBefore(now);
        log.info("Deleted {} expired ads as of {}", deletedCount, now);
    }
}