package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncAdRemove {
    private final AdRepository adRepository;

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Void> removeAd(List<Ad> ads) {
        if (ads.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        List<Long> idsAdForDelete = ads.stream().
                filter(ad -> ad.getEndDate().isBefore(LocalDateTime.now()) || ad.getAppearancesLeft() == 0)
                .map(Ad::getId)
                .toList();
        try {
            if (!idsAdForDelete.isEmpty()) {
                for (Long id : idsAdForDelete) {
                    adRepository.deleteById(id);
                }
                log.info("Expired advertisements have been deleted");
            } else {
                log.info("No expired advertisements found to delete.");
            }
        } catch (Exception e) {
            log.error("Error when deleting expired advertisements", e);
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }
}
