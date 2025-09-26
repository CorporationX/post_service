package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final ExecutorService executorService;

    @Override
    public void removeExpiredAds(int batchSize) {
        List<List<Ad>> batches = splitIntoBatches(adRepository.findExpiredAds(), batchSize);
        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> removeAds(batch), executorService))
                .toList();
        CompletableFuture<Void> deleteAdsFeatures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        try {
            deleteAdsFeatures.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Deleting ads error", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private List<List<Ad>> splitIntoBatches(List<Ad> adsList, int batchSize) {
        return IntStream.range(0, (adsList.size() + batchSize - 1) / batchSize)
                .mapToObj(value -> adsList.subList(value * batchSize, Math.min(adsList.size(), (value + 1) * batchSize)))
                .toList();
    }

    private void removeAds(List<Ad> batch) {
        adRepository.deleteAll(batch);
        log.debug("Removing ads list {} - Finished", batch);
    }
}