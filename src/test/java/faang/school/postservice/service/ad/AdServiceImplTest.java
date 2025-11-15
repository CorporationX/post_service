package faang.school.postservice.service.ad;

import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {
    @Mock
    private AdRepository adRepository;
    @InjectMocks
    private AdServiceImpl adService;

    @Test
    public void removeAds_shouldCreatePageAndInvoke() {
        adService.removeAds();

        verify(adRepository).deleteAllByEndDate(any(LocalDateTime.class));
    }
}