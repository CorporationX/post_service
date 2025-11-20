package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.ad.AdService;
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

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private ThreadPoolTaskExecutor executor;

    @InjectMocks
    private AdService adService;

    Ad ad1 = Ad.builder().id(1L).appearancesLeft(0).endDate(LocalDateTime.now().plusDays(1)).build();
    Ad ad2 = Ad.builder().id(2L).appearancesLeft(0).endDate(LocalDateTime.now().plusDays(1)).build();
    Ad ad3 = Ad.builder().id(3L).appearancesLeft(0).endDate(LocalDateTime.now().plusDays(1)).build();
    Ad ad4 = Ad.builder().id(4L).appearancesLeft(2).endDate(LocalDateTime.now().minusDays(1)).build();
    Ad ad5 = Ad.builder().id(5L).appearancesLeft(3).endDate(LocalDateTime.now().minusDays(1)).build();

    @BeforeEach
    void setup() {
        adService = new AdService(adRepository, executor);
    }

    @Test
    void testClearExpiredAdsSuccess() {
        ReflectionTestUtils.setField(adService, "chunkSize", 1000);

        List<Ad> testAdList = List.of(ad1, ad2, ad3, ad4, ad5);

        Mockito.when(adRepository.findAll()).thenReturn(testAdList);
        adService.clearExpiredAds();

        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(executor).execute(captor.capture());
        captor.getValue().run();

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
