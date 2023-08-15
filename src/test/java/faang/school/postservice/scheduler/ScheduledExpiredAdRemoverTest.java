package faang.school.postservice.scheduler;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ScheduledExpiredAdRemoverTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private ThreadPoolTaskExecutor executor;

    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;

    @BeforeEach
    void setUp() {
        scheduledExpiredAdRemover = new ScheduledExpiredAdRemover(2, executor, adRepository);
    }

    @Test
    void scheduledExpiredAdRemoverTest() {
        Ad firstAd = Ad.builder()
                .id(1)
                .build();
        Ad secondAd = Ad.builder()
                .id(2)
                .build();
        Ad thirddAd = Ad.builder()
                .id(3)
                .build();
        List<Ad> returnList = List.of(firstAd, secondAd, thirddAd);

        when(adRepository.findExpiredAds()).thenReturn(returnList);

        scheduledExpiredAdRemover.removeExpiredAds();

        verify(adRepository).findExpiredAds();
        verify(executor, times(2)).execute(any());
    }
}