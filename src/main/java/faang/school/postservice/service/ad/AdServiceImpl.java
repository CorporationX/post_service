package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;

    @Override
    @Async("adRemoverExecutorService")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAds(List<Ad> ads) {
        if (ads == null || ads.isEmpty()) {
            log.warn("Попытка удалить пустой или null список объявлений");
            throw new IllegalArgumentException("Список объявлений не может быть null или пустым");
        }

        try {
            adRepository.deleteAll(ads);
            log.info("Успешно удалено {} объявлений", ads.size());
        } catch (DataAccessException e) {
            log.error("Произошла ошибка при удалении объявлений", e);
            throw new RuntimeException("Не удалось удалить объявления", e);
        }
    }
}