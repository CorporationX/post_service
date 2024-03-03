package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.service.scheduler.ScheduledExpiredAdRemover;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledExpiredAdRemoverTest {
    @Mock
    private AdService adService;
    @InjectMocks
    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;
    private List<Ad> ads;

    @BeforeEach
    void setUp() {
        ads = List.of(
                Ad.builder().id(1L).endDate(LocalDateTime.now().minusDays(1)).build(),
                Ad.builder().id(2L).endDate(LocalDateTime.now().minusDays(2)).build());
    }

    @Test
    void shouldRemoveExpiredAds() {
        ReflectionTestUtils.setField(scheduledExpiredAdRemover, "expiredAdBatchSize", 1);
        when(adService.findExpiredAds()).thenReturn(ads);
        scheduledExpiredAdRemover.removeExpiredAds();
        verify(adService, times(2)).removeExpiredAds(any());
    }
}