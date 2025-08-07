package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class AdService {
    private final AdRepository adRepository;
    private final Integer batchSize;
    private final ExecutorService adRemoverThreadPool;

    public AdService(AdRepository adRepository,
                     @Value("${ad-remover.batch-size}")
                     Integer batchSize,
                     ExecutorService adRemoverThreadPool) {
        this.adRepository = adRepository;
        this.batchSize = batchSize;
        this.adRemoverThreadPool = adRemoverThreadPool;
    }

    public void removeDueAds() {
        List<Ad> allAdsList = StreamSupport
                .stream(adRepository.findAll().spliterator(), false)
                .toList();
        if (allAdsList.isEmpty()) {
            log.info("No ads found for clean-up");
        } else {
            List<List<Ad>> partitions = partition(allAdsList, batchSize);
            List<CompletableFuture<Void>> futures = partitions.stream()
                    .map(subList -> CompletableFuture.runAsync(() ->
                                    deleteAdFromSublist(subList), adRemoverThreadPool)
                            .exceptionally(ex -> {
                                log.error("Error cleaning due ads");
                                return null;
                            })
                    )
                    .toList();
            futures.forEach(CompletableFuture::join);
            try {
                adRemoverThreadPool.shutdown();
                if (!adRemoverThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    adRemoverThreadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                adRemoverThreadPool.shutdownNow();
            }
        }
    }

    private void deleteAdFromSublist(List<Ad> adList) {
        adList.stream().filter(s -> s.getEndDate().isBefore(LocalDateTime.now()) || s.getAppearancesLeft() == 0)
                .forEach(adRepository::delete);
    }

    private <T> List<List<T>> partition(List<T> completeList, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < completeList.size(); i += batchSize) {
            partitions.add(completeList.subList(i, Math.min(i + batchSize, completeList.size())));
        }
        return partitions;
    }
}
