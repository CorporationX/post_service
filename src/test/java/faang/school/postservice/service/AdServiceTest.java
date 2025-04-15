package faang.school.postservice.service;

import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {

    @Mock
    AdRepository adRepository;

    AdService adService;

    @BeforeEach
    void setUp() throws Exception {
        adService = new AdService(adRepository);

        Field field = AdService.class.getDeclaredField("deleteExpiredPostsThreads");
        field.setAccessible(true);
        field.set(adService, 3);
    }


    @Test
    @DisplayName("Negative: test when expired post not found")
    void findExpiredPostNegativeNoPost() {
        when(adRepository.findExpiredPostByDateEnd(any())).thenReturn(Collections.emptyList());

        adService.findExpiredPostByDateEnd();

        verify(adRepository, never()).deleteById(any());
    }

    @Test
    void findExpiredPostSuccess() {
        List<Long> expiredIds = List.of(1L, 2L, 3L);

        when(adRepository.findExpiredPostByDateEnd(any())).thenReturn(expiredIds);

        adService.findExpiredPostByDateEnd();

        verify(adRepository, times(3)).deleteById(any(Long.class));
    }

}
