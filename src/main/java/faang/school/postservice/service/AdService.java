package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {
    private final AdRepository adRepository;

    @Value("${post.ad-remover.batch-size}")
    private int expiredAdBatchSize;

    public Optional<List<List<Ad>>> findExpiredAds() {
        log.info("Scheduled expired ads removing");
        List<Ad> expiredAds = adRepository.findByEndDateBefore(LocalDateTime.now());

        if (!expiredAds.isEmpty()) {
            return Optional.of(ListUtils.partition(expiredAds, expiredAdBatchSize));
        }
        return Optional.empty();
    }

    @Async("executorService")
    @Transactional
    public void removeExpiredAds(List<Ad> expiredAds) {
        adRepository.deleteAllInBatch(expiredAds);
    }
}
