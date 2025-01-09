package faang.school.postservice.scheduler.ad;
import faang.school.postservice.service.ad.AdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExpiredAdRemoverTest {

    @Mock
    private AdService adService;

    @InjectMocks
    private ExpiredAdRemover remover;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(remover, "batchSize", 3);
    }

    @Test
    public void testDeleteExpireAdsPositive() throws InterruptedException {

        List<Long> expiredAdIds = Arrays.asList(1L, 2L, 3L);
        when(adService.findExpiredAdIds()).thenReturn(expiredAdIds);

        remover.deleteExpiredAds();

        Thread.sleep(1000);

        verify(adService, times(1)).deleteAdByIds(expiredAdIds);
    }

    @Test
    public void testDeleteEmptyList() {
        when(adService.findExpiredAdIds()).thenReturn(Collections.emptyList());

        remover.deleteExpiredAds();

        verify(adService, never()).deleteAdByIds(any());
    }
}
