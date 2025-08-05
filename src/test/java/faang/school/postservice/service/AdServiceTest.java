package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AdServiceTest {
    private AdService adService;
    @Mock
    private AdRepository adRepository;
    @Captor
    private ArgumentCaptor<Ad> adCaptor;

    @BeforeEach
    public void setUp() {
        int batchsize = 2;
        adService = new AdService(adRepository, batchsize,
                Executors.newFixedThreadPool(3));
    }

    @Test
    public void removeAdsTest() throws InterruptedException {
        Ad firstAd = getAdBuild(1,
                LocalDateTime.of(2025, 7, 3, 0, 0),
                2);
        Ad secondAd = getAdBuild(2,
                LocalDateTime.of(2025, 9, 3, 0, 0),
                0);
        Ad thirdAd = getAdBuild(3,
                LocalDateTime.of(2025, 10, 3, 0, 0),
                2);
        Ad fourthAd = getAdBuild(4,
                LocalDateTime.of(2025, 11, 3, 0, 0),
                2);

        Iterable<Ad> ads = List.of(firstAd, secondAd, thirdAd, fourthAd);

        when(adRepository.findAll()).thenReturn(ads);

        adService.removeDueAds();
        Thread.sleep(1000);

        Mockito.verify(adRepository, times(2))
                .delete(adCaptor.capture());
        List<Ad> deletedAds = adCaptor.getAllValues();
        assertTrue(deletedAds.contains(firstAd));
        assertTrue(deletedAds.contains(secondAd));
    }

    @Test
    public void noAdsToRemoveTest() throws InterruptedException {
        Iterable<Ad> emptyAdsList = Collections.emptyList();
        when(adRepository.findAll()).thenReturn(emptyAdsList);

        adService.removeDueAds();
        Thread.sleep(1000);

        Mockito.verify(adRepository, times(0))
                .delete(any());
    }

    private Ad getAdBuild(int id, LocalDateTime endDate, int appearancesLeft) {
        return Ad.builder()
                .id(id)
                .endDate(endDate)
                .appearancesLeft(appearancesLeft)
                .build();
    }

}
