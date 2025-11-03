package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdServiceImplTest {

    private final int maxDeletePerThread = 2;
    private final ExecutorService deleteAdsPool = Executors.newFixedThreadPool(2);

    @Mock
    private AdRepository adRepository;



    @InjectMocks
    private AdServiceImpl adService;

    @Captor
    ArgumentCaptor<List<Ad>> adListArgumentCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adService, "maxDeletePerThread", maxDeletePerThread);
        ReflectionTestUtils.setField(adService, "deleteAdsPool", deleteAdsPool);
    }

    @Test
    void testDeleteExpiredAds() {
        Ad adOne = Ad.builder()
                .id(1L)
                .endDate(LocalDateTime.now().plusDays(1))
                .appearancesLeft(1)
                .build();

        Ad adTwo = Ad.builder()
                .id(2L)
                .endDate(LocalDateTime.now().plusDays(1))
                .appearancesLeft(0)
                .build();

        Ad adThree = Ad.builder()
                .id(3L)
                .endDate(LocalDateTime.now().minusDays(1))
                .appearancesLeft(1)
                .build();

        Ad adFour = Ad.builder()
                .id(4L)
                .endDate(LocalDateTime.now().minusDays(1))
                .appearancesLeft(0)
                .build();

        List<Ad> ads = List.of(adOne, adTwo, adThree, adFour);
        List<Long> expiredAddsIds = Stream.of(adTwo, adThree, adFour).map(Ad::getId).toList();

        when(adRepository.findAll()).thenReturn(ads);
        adService.deleteExpiredAds();

        verify(adRepository, times(2)).deleteAll(adListArgumentCaptor.capture());

        List<Long> capturedAdsIds = adListArgumentCaptor.getAllValues().stream()
                .flatMap(Collection::stream)
                .map(Ad::getId).toList();

        assertEquals(3, capturedAdsIds.size());
        assertTrue(capturedAdsIds.containsAll(expiredAddsIds));
    }
}