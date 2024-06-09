package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {
    @InjectMocks
    private AdServiceImpl adService;
    @Mock
    private AdRepository adRepository;

    @Test
    void testRemoveExpiredAds_NoExpiredAds() {
        List<Ad> ads = new ArrayList<>();
        ads.add(Ad.builder().id(1).appearancesLeft(1).endDate(LocalDateTime.now().plusDays(2)).build());
        ads.add(Ad.builder().id(2).appearancesLeft(2).endDate(LocalDateTime.now().plusDays(3)).build());

        when(adRepository.findAll()).thenReturn(ads);

        adService.removeExpiredAds(10);

        verify(adRepository, never()).deleteByIdIn(anyList());
    }

    @Test
    void testRemoveExpiredAds_EmptyAdList() {
        List<Ad> ads = new ArrayList<>();

        when(adRepository.findAll()).thenReturn(ads);

        adService.removeExpiredAds(10);

        verify(adRepository, never()).deleteByIdIn(anyList());
    }

    @Test
    void testRemoveExpiredAds_WithExpiredAds() {
        List<Ad> ads = new ArrayList<>();
        ads.add(Ad.builder().id(1).appearancesLeft(0).endDate(LocalDateTime.now().minusDays(2)).build());
        ads.add(Ad.builder().id(2).appearancesLeft(0).endDate(LocalDateTime.now().minusDays(3)).build());

        when(adRepository.findAll()).thenReturn(ads);

        adService.removeExpiredAds(10);

        verify(adRepository, times(1)).deleteByIdIn(List.of(1L, 2L));
    }

    @Test
    void testRemoveExpiredAds_ZeroBatchSize() {
        assertThrows(IllegalArgumentException.class, () ->
            adService.removeExpiredAds(0)
        );
    }


}