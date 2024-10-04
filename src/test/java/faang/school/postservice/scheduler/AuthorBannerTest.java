package faang.school.postservice.scheduler;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorBannerTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private RedisProperties redisProperties;
    @Mock
    private PostService postService;
    @InjectMocks
    private AuthorBanner authorBanner;

    @Test
    public void testBanAuthorsSuccess() {
        List<Long> banAuthorsIds = List.of(1L, 2L, 3L);

        when(postService.getAuthorsWithExcessVerifiedFalsePosts()).thenReturn(banAuthorsIds);

        authorBanner.banAuthors();

        verify(postService, atLeastOnce()).getAuthorsWithExcessVerifiedFalsePosts();
        verify(redisTemplate, atLeastOnce()).convertAndSend(redisProperties.getUserBanChannelName(), banAuthorsIds);
    }
}
