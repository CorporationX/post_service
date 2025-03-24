package faang.school.postservice.service.ad;

import com.google.common.collect.Lists;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdService {
    private final AsyncAdRemove asyncAdRemove;
    private final AdRepository adRepository;

    @Value("${batch.size}")
    public int batchSize;

    public void removingExpiredAds() {
        List<Ad> ads = adRepository.findAll();
        List<List<Ad>> partitions = Lists.partition(ads, batchSize);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (List<Ad> batch : partitions) {
            CompletableFuture<Void> future = asyncAdRemove.removeAd(batch);
            futures.add(future);
        }
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();
        log.info("All expired advertisements have been removed");
    }
}
