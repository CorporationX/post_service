package faang.school.postservice.bjs_77879;

import faang.school.postservice.model.ad.Ad;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final AdCleanupSettings settings;

    private final int BATCH_SIZE;

    public void deleteExpiredAds() {
        LocalDateTime now = LocalDateTime.now();

        List<Ad> expiredAds = adRepository.findAllByEndDateBefore(now);

        if (expiredAds.isEmpty()) {
            return;
        }

        List<Long> expiredIds = expiredAds.stream()
                .map(Ad::getId)
                .collect(Collectors.toList());

        adRepository.deleteByIds(expiredIds);
    }
}