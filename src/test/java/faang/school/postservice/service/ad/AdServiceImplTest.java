package faang.school.postservice.service.ad;

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
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    public void setUp() {
        ReflectionTestUtils.setField(adService, "SIZE", 100);
    }

    @Test
    public void removeAd_invokeProcessingAd_shouldCreatePageAndInvoke() {
        Pageable pageable = PageRequest.of(0, 100);
        List<Long> expectedIds = List.of(1L, 2L);
        Page<Long> page = new PageImpl<>(expectedIds, pageable, 2);

        when(adRepository.findAllByEndDate(eq(pageable), any(LocalDateTime.class)))
                .thenReturn(page);

        adService.removeAd();

        verify(adRepository).findAllByEndDate(eq(pageable), any(LocalDateTime.class));
        verify(processingAd).processingAd(expectedIds);
    }
}