package faang.school.postservice.adService;

import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.postAd.AdService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdService adService;


    @Test
    public void deleteAdPost_ShouldDeleteGivenIds() {
        List<Long> ids = List.of(1L, 2L, 3L);
        when(adRepository.deleteByIds(ids)).thenReturn(ids.size());

        assertDoesNotThrow(() -> adService.deleteAdPosts(ids));

        verify(adRepository, times(1)).deleteByIds(ids);
    }
}
