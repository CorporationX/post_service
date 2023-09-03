package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {
    @Mock
    private AdRepository adRepository;
    private AdService adService;

    @BeforeEach
    public void setup() {
        adService = new AdService(adRepository);
        Mockito.doNothing().when(adRepository).deleteById(Mockito.anyLong());
    }

    @Test
    public void testDeleteAds() {
        List<Ad> ads = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        IntStream.rangeClosed(0, 200).forEach(number -> ads.add(Ad.builder().id(number).endDate(now.minusDays(1)).build()));
        IntStream.rangeClosed(201, 400).forEach(number -> ads.add(Ad.builder().id(number).endDate(now.plusDays(1)).build()));

        Mockito.when(adRepository.findAll())
                .thenReturn(ads);
        adService.deleteOverdueAds();
        LongStream.rangeClosed(0, 200).forEach(number ->
                Mockito.verify(adRepository).deleteById(number));
    }
}