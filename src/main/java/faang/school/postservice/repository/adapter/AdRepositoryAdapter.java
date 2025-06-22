package faang.school.postservice.repository.adapter;

import faang.school.postservice.model.ad.AdStatus;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdRepositoryAdapter {
    private final AdRepository adRepository;

    @Transactional
    public int updateExpiredAds(AdStatus newStatus, AdStatus currentStatus, LocalDateTime now) {
        return adRepository.expireAdByAppearances(newStatus, currentStatus) +
               adRepository.expireAdByEndDate(newStatus, currentStatus, now);
    }

    @Transactional
    public void deletePostAds(List<Long> adIds) {
        adRepository.deletePostAds(adIds);
    }

    @Transactional(readOnly = true)
    public Page<Long> findAdIdsByStatus(AdStatus adStatus, Pageable pageRequest) {
        return adRepository.findAdIdsByStatus(adStatus, pageRequest);
    }
}
