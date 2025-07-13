package faang.school.postservice.bjs_77879;

import faang.school.postservice.model.ad.Ad;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final ExecutorService executor;
    private final AdCleanupSettings settings;

    public void deleteExpiredAds() {
        List<Ad> allAds = adRepository.findAll();

        if (allAds.isEmpty()) return;

        List<Long> expiredIds = allAds.stream()
                .filter(ad -> ad.getEndDate().isBefore(LocalDateTime.now()))
                .map(Ad::getId)
                .toList();

        if (expiredIds.isEmpty()) return;

        int batchSize = settings.getBatchSize();

        List<List<Long>> batches = partitionList(expiredIds, batchSize);

        batches.forEach(ids -> {
            CompletableFuture.runAsync(() -> {
                try {
                    adRepository.deleteByIds(ids);
                } catch (Exception e) {
                    System.err.println("Ошибка при удалении партии ID: " + ids + ". Ошибка: " + e.getMessage());
                }
            }, executor);
        });
    }

    private static <T> List<List<T>> partitionList(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            int end = Math.min(i + size, list.size());
            partitions.add(list.subList(i, end));
        }
        return partitions;
    }
}