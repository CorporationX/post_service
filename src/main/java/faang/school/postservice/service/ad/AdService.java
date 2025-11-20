package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AdService {

    private final AdRepository adRepository;
    private final ThreadPoolTaskExecutor executor;

    public AdService(
            AdRepository adRepository,
            @Qualifier("expiredAdTaskExecutor") ThreadPoolTaskExecutor executor
    ) {
        this.adRepository = adRepository;
        this.executor = executor;
    }

    @Value("${cleanup.chunk-size}")
    private int chunkSize;

    public void clearExpiredAds() {

        List<Long> expiredAdIds = new ArrayList<>();
        for (Ad ad : adRepository.findAll()) {
            if (ad.getEndDate().isBefore(LocalDateTime.now()) || ad.getAppearancesLeft() == 0) {
                expiredAdIds.add(ad.getId());
            }
        }
        if (expiredAdIds.isEmpty()) {
            log.info("No expired ads found.");
            return;
        }

        List<List<Long>> chunks = partitionList(expiredAdIds, chunkSize);

        chunks.forEach((chunk) -> executor.execute(() -> adRepository.deleteAllById(chunk)));
    }

    private List<List<Long>> partitionList(List<Long> list, int chunkSize) {
        List<List<Long>> parts = new ArrayList<>((list.size() + chunkSize - 1) / chunkSize);
        for (int i = 0; i < list.size(); i += chunkSize) {
            parts.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
        }
        return parts;
    }
}