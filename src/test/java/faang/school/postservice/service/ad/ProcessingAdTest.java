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

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessingAdTest {
    @Mock
    private AdRepository adRepository;
    @InjectMocks
    private ProcessingAd processingAd;

    @Test
    public void processingAd_deleteAd_shouldDeleteAd() {
        List<Ad> ads = List.of(preparingAd(1L, 0L), preparingAd(2L, 5L));

        processingAd.processingAd(ads);

        verify(adRepository).deleteByIdsNative(new ArrayList<>(List.of(1L, 2L)));
    }

    public Ad preparingAd(long id, long countViews) {
        return Ad.builder()
                .id(id)
                .appearancesLeft(0)
                .appearancesLeft(countViews)
                .endDate(LocalDateTime.now().minusMonths(2))
                .build();
    }
}