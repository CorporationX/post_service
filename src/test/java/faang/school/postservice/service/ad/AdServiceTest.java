package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {
    @InjectMocks
    private AdService adService;

    @Mock
    private AdRepository adRepository;

    @Test
    public void testFindAllTest() {
        adService.findAll();
        verify(adRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteByIdsTest() {
        List<Long> ids = List.of(1L, 2L, 3L);
        adService.deleteByIds(ids);
        verify(adRepository, times(1)).deleteByIds(ids);
    }

    @Test
    public void testDeleteExpiredAds() {
        LocalDateTime currentDate = LocalDateTime.now();
        Ad firstAd = Ad.builder()
                        .id(1L)
                        .appearancesLeft(12)
                        .endDate(currentDate.minusDays(1))
                        .build();
        Ad secondAd = Ad.builder()
                        .id(2L)
                        .appearancesLeft(0)
                        .endDate(currentDate.plusDays(1))
                        .build();
        Ad thirdAd = Ad.builder()
                        .id(3L)
                        .appearancesLeft(7)
                        .endDate(currentDate.plusDays(3))
                        .build();
        Ad fourthAd = Ad.builder()
                        .id(4L)
                        .appearancesLeft(8)
                        .endDate(currentDate.plusDays(2))
                        .build();
        Ad fifthAd = Ad.builder()
                        .id(5L)
                        .appearancesLeft(0)
                        .endDate(currentDate.plusDays(1))
                        .build();

        List<Ad> ads = List.of(firstAd, secondAd, thirdAd, fourthAd, fifthAd);

        List<Long> idsOfExpiredAds = List.of(1L, 2L, 5L);
        List<List<Long>> expiresAdsByBatches = ListUtils.partition(idsOfExpiredAds, 2);

        adService.setBatchSize(2);
        when(adRepository.findAll()).thenReturn(ads);

        adService.deleteExpiredAds();

        expiresAdsByBatches.forEach(expiresAdsBatch -> verify(adRepository, times(1)).deleteByIds(expiresAdsBatch));
    }
}