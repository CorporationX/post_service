package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduledExpiredAdRemoverTest {

    @Mock
    private AdService adService;

    @InjectMocks
    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;

    @Test
    void testRemoveExpiredAds() {
        scheduledExpiredAdRemover.removeExpiredAds();
        Mockito.verify(adService).removeExpiredAds(Mockito.anyInt());
    }
}