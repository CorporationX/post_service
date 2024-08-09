package faang.school.postservice.service.postAd;

import faang.school.postservice.exception.DeletionFailedException;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {

    @Value("${post.ad-remover.batch-size}")
    private int batchSize;

    @Value("${post.pagination.page-size}")
    private int pageSize;

    private final AdRepository adRepository;


    @Transactional
    public void deleteExpiredAd() {
        int pageNumber = 0;
        List<Long> adIdsToDelete = new ArrayList<>();
        Page<Ad> page = adRepository.findAll(PageRequest.of(pageNumber, pageSize));
        LocalDateTime beforeTime = LocalDateTime.now();

        while (page.hasContent()) {
            adIdsToDelete.addAll(page.getContent().stream()
                    .filter(ad -> ad.getEndDate().isBefore(beforeTime) || ad.getAppearancesLeft() <= 0)
                    .map(Ad::getId)
                    .toList());

            pageNumber++;
            page = adRepository.findAll(PageRequest.of(pageNumber, pageSize));
        }

        if (!adIdsToDelete.isEmpty()) {
            for (int i = 0; i < adIdsToDelete.size(); i += batchSize) {
                List<Long> batch = adIdsToDelete.subList(i, Math.min(i + batchSize, adIdsToDelete.size()));
                deleteAdPosts(batch);
            }
        }
    }

    @Transactional
    public void deleteAdPosts(List<Long> ids) {
        if (!ids.isEmpty()) {
            log.info("deleting {} in thread {}", ids, Thread.currentThread().getName());
            int countOfDeleted = adRepository.deleteByIds(ids);

            log.info("amount of deleted expired post ads: {}", countOfDeleted);

            if (countOfDeleted == 0) {
                throw new DeletionFailedException("Posts deletion haven't been completed");
            }
        }
    }
}
