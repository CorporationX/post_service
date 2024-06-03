package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;

    public void removeExpiredAds(int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than 0");
        }

        LocalDateTime currentDate = LocalDateTime.now();

        List<Ad> allAds = adRepository.findAll();

        List<Ad> expiredAds = allAds.stream()
                .filter(ad -> ad.getEndDate().isBefore(currentDate) || ad.getAppearancesLeft() == 0)
                .toList();

        List<List<Ad>> partitions = partitionList(expiredAds, batchSize);

        if (partitions.isEmpty()) {
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(partitions.size());

        try {
            for (List<Ad> partition : partitions) {
                executor.submit(() -> {
                    List<Long> ids = partition.stream()
                            .map(Ad::getId).toList();
                    adRepository.deleteByIdIn(ids);
                });
            }
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }

    }

    private <T> List<List<T>> partitionList(List<T> list, int size) {
        return IntStream.range(0, (list.size() + size - 1) / size)
                .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())))
                .collect(Collectors.toList());
    }
}
