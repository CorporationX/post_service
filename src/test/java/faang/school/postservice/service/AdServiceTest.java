package faang.school.postservice.service;

import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.ad.AdCleanupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private ThreadPoolTaskExecutor executor;

    @InjectMocks
    private AdCleanupService adCleanupService;

    @BeforeEach
    void setup() {
        adCleanupService = new AdCleanupService(adRepository, executor);
    }

    @Test
    void testClearExpiredAdsSuccess() {
        ReflectionTestUtils.setField(adCleanupService, "chunkSize", 1000);
        List<Long> testAdIds = List.of(1L, 2L, 3L, 4L, 5L);

        Mockito.when(adRepository.findExpiredAdIds()).thenReturn(testAdIds);
        adCleanupService.clearExpiredAds();

        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(executor).execute(captor.capture());
        captor.getValue().run();

        Mockito.verify(adRepository).deleteAllById(testAdIds);
    }

    @Test
    void testClearExpiredAdsListIsEmpty() {
        ReflectionTestUtils.setField(adCleanupService, "chunkSize", 1000);

        List<Long> emptyList = Collections.emptyList();
        Mockito.when(adRepository.findExpiredAdIds()).thenReturn(emptyList);
        adCleanupService.clearExpiredAds();

        Mockito.verify(adRepository, Mockito.never()).deleteAllById(Mockito.anyList());
    }
}
