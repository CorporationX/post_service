package faang.school.postservice.service.post;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledExpiredAdRemoverTest {

    private final Ad ad1 = new Ad();
    private final Ad ad2 = new Ad();
    private final Ad ad3 = new Ad();
    private final Ad ad4 = new Ad();
    private List<Ad> ads;

    @InjectMocks
    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;

    @Mock
    private AdRepository adRepository;

    @BeforeEach
    public void init() {
        ad1.setAppearancesLeft(1L);
        ad1.setEndDate(LocalDateTime.of(2024, Month.OCTOBER, 11, 12, 12));
        ad2.setAppearancesLeft(0L);
        ad2.setEndDate(LocalDateTime.of(2024, Month.OCTOBER, 11, 12, 12));
        ad3.setAppearancesLeft(1L);
        ad3.setEndDate(LocalDateTime.now().minusDays(1L));
        ad4.setAppearancesLeft(0L);
        ad4.setEndDate(LocalDateTime.now().minusDays(1L));
        ReflectionTestUtils.setField(scheduledExpiredAdRemover, "subListSize", 100);

    }

    @Test
    void shouldReturnAmountOfInvokesWhenDeleteExpiredAdsTest() {
        //arrange
        ads = List.of(ad1, ad2, ad3, ad4);

        //act
        when(adRepository.findAll()).thenReturn(ads);
        scheduledExpiredAdRemover.scheduledDeleteExpiredAds();

        //assert
        verify(adRepository, times(3)).deleteById(anyLong());
    }

    @Test
    void shouldReturnNullAmountOfInvokesWhenDeleteExpiredAdsTest() {
        //arrange
        ads = List.of(ad1);

        //act
        when(adRepository.findAll()).thenReturn(ads);
        scheduledExpiredAdRemover.scheduledDeleteExpiredAds();

        //assert
        verify(adRepository, never()).deleteById(anyLong());
    }
}