package faang.school.postservice.scheduler;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorBannerTest {

    @Mock
    private PostService postService;

    @Mock
    private RedisTemplate<String, List<Long>> redisTemplate;

    @Spy
    private RedisProperties redisProperties;

    @InjectMocks
    private AuthorBannerScheduler authorBanner;

    private final String channelName = "channel";

    @BeforeEach
    void setUp() {
        Map<String, String> channels = Map.of(
                "user-service", channelName
        );
        redisProperties.setChannels(channels);
    }

    @Test
    void testBanUser() {
        List<Long> violatorIds = List.of(1L, 2L, 3L);
        when(postService.getAuthorsWithMoreFiveUnverifiedPosts()).thenReturn(violatorIds);

        authorBanner.banUser();

        verify(postService).getAuthorsWithMoreFiveUnverifiedPosts();
        verify(redisTemplate).convertAndSend(channelName, violatorIds);
    }

    @Test
    void testBanUserWithEmptyList() {
        when(postService.getAuthorsWithMoreFiveUnverifiedPosts()).thenReturn(Collections.emptyList());

        authorBanner.banUser();

        verify(postService).getAuthorsWithMoreFiveUnverifiedPosts();
        verify(redisTemplate).convertAndSend(channelName, Collections.emptyList());
    }
}
