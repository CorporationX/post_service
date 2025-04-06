package faang.school.postservice.service.Ad;

import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.validator.AdValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private AdValidator adValidator;

    @Mock
    private ThreadPoolTaskExecutor taskExecutor;

    @Mock
    private Future<Object> futureMock;

    @InjectMocks
    private AdServiceImpl adService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adService, "batchSize", 100);
    }

    @Test
    @DisplayName("Test: Given expired ads exist, should delete expired ads")
    void testRemoveValidExpiredAds() throws ExecutionException, InterruptedException {
        List<Long> expiredAdIds = List.of(1L, 2L, 3L);

        when(adRepository.findExpiredAdIds(any(LocalDateTime.class)))
                .thenReturn(expiredAdIds);
        when(taskExecutor.submit(Mockito.<Callable<Object>>any()))
                .thenAnswer(invocation -> {
                    invocation.<Callable<Object>>getArgument(0).call();
                    return futureMock;
                });

        adService.removeExpiredAds();

        verify(adRepository).findExpiredAdIds(any(LocalDateTime.class));
        verify(adValidator).validateAdIds(expiredAdIds);
        verify(adRepository).deleteByIds(expiredAdIds);
        verify(futureMock).get();
    }

    @Test
    @DisplayName("Test: Given no expired ads exist, should throw exception")
    void testRemoveExpiredAdsWhenNoExpiredAds() {
        when(adRepository.findExpiredAdIds(any(LocalDateTime.class))).thenReturn(List.of());

        adService.removeExpiredAds();

        verify(adRepository, never()).deleteByIds(anyList());
        verify(taskExecutor, never()).submit(any(Runnable.class));
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
