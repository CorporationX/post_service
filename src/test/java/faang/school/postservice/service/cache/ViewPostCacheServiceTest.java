package faang.school.postservice.service.cache;

import faang.school.postservice.repository.cache.CacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViewPostCacheServiceTest {

    @InjectMocks
    private ViewPostCacheService viewPostCacheService;

    @Mock
    private CacheRepository<Long> cacheRepository;

    private Long postId;
    private Long viewAuthorId;
    private String viewKey;

    @BeforeEach
    public void setUp() {
        postId = 123L;
        viewAuthorId = 456L;
        viewKey = postId + "::count_post_view";
    }

    @Test
    public void testSave_ShouldIncrementViewCount_WhenCalled() {
        when(cacheRepository.incrementAndGet(viewKey)).thenReturn(1L);

        viewPostCacheService.save(postId, viewAuthorId);

        verify(cacheRepository).incrementAndGet(viewKey);
    }

    @Test
    public void testGet_ShouldReturnZero_WhenNoDataInCache() {
        when(cacheRepository.get(viewKey, Long.class)).thenReturn(Optional.empty());

        Long result = viewPostCacheService.get(postId);

        assertEquals(0L, result);
        verify(cacheRepository).get(viewKey, Long.class);
    }

    @Test
    public void testGet_ShouldReturnViewCount_WhenDataExistsInCache() {
        Long correctResult = 5L;
        when(cacheRepository.get(viewKey, Long.class)).thenReturn(Optional.of(correctResult));

        Long result = viewPostCacheService.get(postId);

        assertEquals(correctResult, result);
        verify(cacheRepository).get(viewKey, Long.class);
    }
}
