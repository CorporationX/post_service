package faang.school.postservice.service.impl.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdServiceImpl adService;

    private Ad ad;

    @BeforeEach
    void setUp() {
        ad = Ad.builder()
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    @DisplayName("Remove Expired Ads with no empty list")
    void testRemoveExpiredAds() {
        Ad ad2 = Ad.builder()
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();
        var adList = new ArrayList<>(List.of(ad, ad2));

        doReturn(adList).when(adRepository).findAllByEndDateBefore(any(LocalDateTime.class));
        adService.removeExpiredAds(50);

        verify(adRepository).findAllByEndDateBefore(any(LocalDateTime.class));
        verify(adRepository).deleteAllInBatch(adList);
    }

    @Test
    @DisplayName("Remove Expired Ads with empty list")
    void testRemoveExpiredAdsEmptyList() {
        doReturn(Collections.emptyList()).when(adRepository).findAllByEndDateBefore(any(LocalDateTime.class));
        adService.removeExpiredAds(50);
        verify(adRepository, never()).deleteAllInBatch(anyList());
    }

    @Test
    @DisplayName("Delete Expired Ads")
    void testDeleteExpiredAdsByBatch() {
        var batch = List.of(ad);
        adService.deleteExpiredAdsByBatch(batch);
        verify(adRepository).deleteAllInBatch(batch);
    }
}