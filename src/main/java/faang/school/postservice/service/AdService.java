package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AdService {
    @Value("${post.ad-remover.scheduler.batchSize}")
    private int batchSize;
    private final AdRepository adRepository;
    private final ThreadPoolExecutor adRemoverThreadPool;

    @Transactional
    public void removeExpiredAds() {
        List<Ad> ads = StreamSupport.stream(adRepository.findAll().spliterator(), false)
                .filter(ad -> ad.getEndDate().isBefore(LocalDateTime.now()) || ad.getAppearancesLeft() == 0)
                .toList();
        for (int i = 0; i < ads.size(); i += batchSize) {
            List<Ad> batch = ads.subList(i, Math.min(i + batchSize, ads.size()));
            CompletableFuture.runAsync(() -> adRepository.deleteAll(batch), adRemoverThreadPool);
        }
    }
}
