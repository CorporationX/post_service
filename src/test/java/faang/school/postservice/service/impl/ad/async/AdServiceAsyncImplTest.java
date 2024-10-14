package faang.school.postservice.service.impl.ad.async;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AdServiceAsyncImplTest {
    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdServiceAsyncImpl adServiceAsyncImpl;

    @Test
    @DisplayName("Delete Expired Ads By Batch")
    void testDeleteExpiredAdsByBatch() {
        var ads = List.of(new Ad(), new Ad());
        adServiceAsyncImpl.deleteExpiredAdsByBatch(ads);
        verify(adRepository).deleteAllInBatch(ads);
        verifyNoMoreInteractions(adRepository);
    }
}