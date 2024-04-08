package faang.school.postservice.service;

import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdService adService;

    @Test
    void testWhenNoAdsToRemoveThenNoneDeleted() {
        adRepository.deleteAll();
        adService.removeExpiredAds();
        long count = adRepository.count();
        assertEquals(0, count);
    }
}