package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.adservice.AdService;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {
    @Mock
    private AdRepository adRepository;
    @InjectMocks
    private AdService adService;
    @Value("${batchSize.batch}")
    private int size;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(adService, "batchSize", 10);
    }

    @Test
    public void testAdsWhichNeedDelete() {
        Ad ad1 = new Ad();
        ad1.setId(1L);
        ad1.setAppearancesLeft(1);
        ad1.setStartDate(LocalDateTime.of(2023, 4, 1, 0, 0));
        ad1.setEndDate(LocalDateTime.of(2024, 4, 1, 0, 0));

        Ad ad2 = new Ad();
        ad2.setId(2L);
        ad2.setAppearancesLeft(5);
        ad2.setStartDate(LocalDateTime.of(2023, 4, 1, 0, 0));
        ad2.setEndDate(LocalDateTime.of(2024, 4, 15, 0, 0));

        List<Ad> ads = new ArrayList<>(List.of(ad1, ad2));

        Mockito.when(adRepository.findAll()).thenReturn(ads);

        adService.deleteAdsWhichEndPaidPeriod();

        Mockito.verify(adRepository, Mockito.times(1)).findAll();
        Mockito.verify(adRepository, Mockito.times(1)).deleteAllById(List.of(1L));
    }

    @Test
    public void testDeleteAdsPeriodEmptyList() {
        Mockito.when(adRepository.findAll()).thenReturn(Collections.emptyList());
        adService.deleteAdsWhichEndPaidPeriod();
        Mockito.verify(adRepository, Mockito.never()).deleteAllById(Mockito.anyList());
    }

    @Test
    public void testStartDeleteAds() {
        List<Long> adIds = List.of(1L, 2L, 3L);

        adService.startDeleteAds(adIds);

        Mockito.verify(adRepository, Mockito.times(1)).deleteAllById(adIds);
    }
}
