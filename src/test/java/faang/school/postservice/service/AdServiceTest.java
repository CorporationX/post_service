package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {
    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdService adService;
    private List<List<Ad>> ads = new ArrayList<>();
    private List<Ad> bucket;

    @BeforeEach
    void setUp() {
        bucket = List.of(
                Ad.builder().id(1L).endDate(LocalDateTime.now().minusDays(1)).build(),
                Ad.builder().id(2L).endDate(LocalDateTime.now().minusDays(2)).build(),
                Ad.builder().id(3L).endDate(LocalDateTime.now().minusDays(1)).build());
        ads.add(bucket);
    }

    @Test
    void shouldFindExpiredAds() {
        ReflectionTestUtils.setField(adService, "expiredAdBatchSize", 1);
        when(adRepository.findByEndDateBefore(any())).thenReturn(bucket);

        Optional<List<List<Ad>>> expiredAds = adService.findExpiredAds();

        assertEquals(3, expiredAds.get().size());
        assertEquals(1, expiredAds.get().get(0).get(0).getId());
        assertEquals(2, expiredAds.get().get(1).get(0).getId());
        assertEquals(3, expiredAds.get().get(2).get(0).getId());
    }

    @Test
    void shouldRemoveExpiredAds() {
        adService.removeExpiredAds(bucket);
        verify(adRepository, times(1)).deleteAllInBatch(bucket);
    }
}