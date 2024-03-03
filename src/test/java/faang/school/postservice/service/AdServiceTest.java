package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {
    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdService adService;
    private List<Ad> ads;
    @BeforeEach
    void setUp() {
        ads = List.of(
                Ad.builder().id(1L).endDate(LocalDateTime.now().minusDays(1)).build(),
                Ad.builder().id(2L).endDate(LocalDateTime.now().minusDays(2)).build(),
                Ad.builder().id(3L).endDate(LocalDateTime.now().plusDays(1)).build());
    }

    @Test
    void shouldFindExpiredAds() {
        when(adRepository.findAll()).thenReturn(ads);
        List<Ad> expiredAds = adService.findExpiredAds();
        assertEquals(2, expiredAds.size());
        assertEquals(ads.get(0).getId(), expiredAds.get(0).getId());
        assertEquals(ads.get(1).getId(), expiredAds.get(1).getId());
    }

    @Test
    void shouldRemoveExpiredAds() {
        adService.removeExpiredAds(ads);
        verify(adRepository, times(1)).deleteAll(ads);
    }
}