package faang.school.postservice.service.postAd;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {

    @Value("${post.ad-remover.batch-size}")
    private int BATCH_SIZE;

    @Value("${post.pagination.page-size}")
    private int PAGE_SIZE;

    private final AdRepository adRepository;

    @Qualifier("adRemover")
    private final ExecutorService executorService;

    @Transactional
    public void deleteExpiredAd() {
        int pageNumber = 0;
        List<Long> adIdsToDelete = new ArrayList<>();
        Page<Ad> page = adRepository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));

        while (page.hasContent()) {
            adIdsToDelete.addAll(page.getContent().stream()
                    .filter(ad -> ad.getEndDate().isBefore(LocalDateTime.now()) || ad.getAppearancesLeft() <= 0)
                    .map(Ad::getId)
                    .toList());

            pageNumber++;
            page = adRepository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
        }

        if (!adIdsToDelete.isEmpty()) {
            for (int i = 0; i < adIdsToDelete.size(); i += BATCH_SIZE) {
                List<Long> batch = adIdsToDelete.subList(i, Math.min(i + BATCH_SIZE, adIdsToDelete.size()));
                CompletableFuture.runAsync(() -> adRepository.deleteByIds(batch), executorService);
            }
        }
    }

    @Transactional
    public void deleteAdPost(List<Long> ids) {
        log.info("deleting {} in thread {}", ids, Thread.currentThread().getName());
        adRepository.deleteByIds(ids);
    }
}
