package faang.school.postservice.service.Ad;

import faang.school.postservice.config.ad.AdDeletionProperties;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.validator.AdValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {

    @Mock
    private AdDeletionProperties adDeletionProperties;

    @Mock
    private AdRepository adRepository;

    @Mock
    private AdValidator adValidator;

    private ThreadPoolTaskExecutor taskExecutor;

    private AdServiceImpl adService;

    @BeforeEach
    void setUp() {
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(2);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.initialize();

        adService = new AdServiceImpl(adRepository, adValidator, taskExecutor, adDeletionProperties);
    }

    @Test
    @DisplayName("Test: Given expired ads exist, should delete expired ads")
    void testRemoveValidExpiredAds() {
        List<Long> expiredAdIds = List.of(1L, 2L, 3L);

        when(adDeletionProperties.getBatchSize()).thenReturn(100);
        when(adRepository.findExpiredAdIds(any(LocalDateTime.class)))
                .thenReturn(expiredAdIds);

        adService.removeExpiredAds();

        verify(adRepository).findExpiredAdIds(any(LocalDateTime.class));
        verify(adValidator).validateAdIds(expiredAdIds);
        verify(adRepository).deleteByIds(expiredAdIds);
    }

    @Test
    @DisplayName("Test: Given no expired ads exist, should not delete anything")
    void testRemoveExpiredAdsWhenNoExpiredAds() {
        when(adRepository.findExpiredAdIds(any(LocalDateTime.class))).thenReturn(List.of());

        adService.removeExpiredAds();

        verify(adRepository, never()).deleteByIds(anyList());
    }

    @Test
    @DisplayName("Test: Given valid ad ID, should validate and delete ad")
    void testDeleteAdByValidId() {
        long adId = 5L;
        doNothing().when(adValidator).validateAdId(adId);
        doNothing().when(adRepository).deleteById(adId);

        adService.deleteAdById(adId);

        verify(adValidator).validateAdId(adId);
        verify(adRepository).deleteById(adId);
    }
}
