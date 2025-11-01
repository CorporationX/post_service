package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {
    @Mock
    private AdRepository adRepository;
    @Mock
    private ProcessingAd processingAd;
    @InjectMocks
    private AdServiceImpl adService;

    @BeforeEach
    public void setUp(){
        ReflectionTestUtils.setField(adService, "SIZE", 100);
    }

    @Test
    public void removeAd_invokeProcessingAd_shouldCreatePageAndInvoke() {
        List<Ad> ads = List.of(preparingAd(1, 0), preparingAd(2, 5));
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by("endDate"));
        Page<Ad> page = new PageImpl<>(ads, pageRequest, ads.size());

        when(adRepository.findAll(pageRequest)).thenReturn(page);
        adService.removeAd();
        verify(adRepository).findAll(pageRequest);
        verify(processingAd).processingAd(ads);
    }

    public Ad preparingAd(long id, long countViews) {
        return Ad.builder()
                .id(id)
                .appearancesLeft(0)
                .appearancesLeft(countViews)
                .endDate(LocalDateTime.now().minusMonths(2))
                .build();
    }

}