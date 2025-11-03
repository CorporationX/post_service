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
        processingAd.processingAd(List.of(1L, 2L));

        verify(adRepository).deleteAllByIdInBatch(new ArrayList<>(List.of(1L, 2L)));
    }
}