package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;

    public List<Long> findExpiredAdIds() {

        return adRepository.findAll().stream()
                .filter(ad -> ad.getAppearancesLeft() < 1 || ad.getEndDate().isBefore(LocalDateTime.now()))
                .map(Ad::getId)
                .toList();
    }

    public void deleteAdByIds(List<Long> ids) {
        adRepository.deleteAllById(ids);
    }
}
