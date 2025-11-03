package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.ad.AdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdService adService;

    Ad ad1 = Mockito.mock(Ad.class);
    Ad ad2 = Mockito.mock(Ad.class);
    Ad ad3 = Mockito.mock(Ad.class);
    Ad ad4 = Mockito.mock(Ad.class);
    Ad ad5 = Mockito.mock(Ad.class);

    @BeforeEach
    void setUp() {
        ad1.setId(1L);
        ad1.setAppearancesLeft(0);
        ad1.setEndDate(LocalDateTime.now().plusDays(1));
        ad2.setId(2L);
        ad2.setAppearancesLeft(0);
        ad2.setEndDate(LocalDateTime.now().plusDays(1));
        ad3.setId(3L);
        ad3.setAppearancesLeft(0);
        ad3.setEndDate(LocalDateTime.now().plusDays(1));
        ad4.setId(4L);
        ad4.setAppearancesLeft(2);
        ad4.setEndDate(LocalDateTime.now().minusDays(1));
        ad5.setId(5L);
        ad5.setAppearancesLeft(3);
        ad5.setEndDate(LocalDateTime.now().minusDays(1));
    }

    @Test
    void testClearExpiredAdsSuccess() {
        ReflectionTestUtils.setField(adService, "chunkSize", 1000);

        List<Ad> testAdList = List.of(ad1, ad2, ad3, ad4, ad5);

        Mockito.when(adRepository.findAll()).thenReturn(testAdList);
        adService.clearExpiredAds();

        List<Long> testAdIds = List
                .of(ad1.getId(), ad2.getId(), ad3.getId(), ad4.getId(), ad5.getId());
        Mockito.verify(adRepository).deleteAllById(testAdIds);
    }

    @Test
    void testClearExpiredAdsListIsEmpty() {
        ReflectionTestUtils.setField(adService, "chunkSize", 1000);

        List<Ad> testAdList = List.of(ad1, ad2, ad3, ad4, ad5);
        testAdList.forEach((ad)->ad.setAppearancesLeft(2));
        testAdList.forEach((ad)->ad.setEndDate(LocalDateTime.now().plusDays(2)));

        Mockito.when(adRepository.findAll()).thenReturn(testAdList);

        adService.clearExpiredAds();

        Mockito.verify(adRepository, Mockito.never()).deleteAllById(Mockito.anyList());
    }
}
