package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.scheduler.ScheduledExpiredAdRemover;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledExpiredAdRemoverTest {
    @Mock
    private AdService adService;
    @InjectMocks
    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;
    private List<List<Ad>> ads = new ArrayList<>();

    @BeforeEach
    void setUp() {
        List<Ad> bucket = List.of(
                Ad.builder().id(1L).endDate(LocalDateTime.now().minusDays(1)).build(),
                Ad.builder().id(2L).endDate(LocalDateTime.now().minusDays(2)).build());
        ads.add(bucket);
    }

    @Test
    void shouldRemoveExpiredAds() {
        when(adService.findExpiredAds()).thenReturn(Optional.of(ads));
        scheduledExpiredAdRemover.removeExpiredAds();
        verify(adService, times(1)).removeExpiredAds(any());
    }
}