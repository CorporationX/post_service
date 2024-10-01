package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorBannerTest {

    @Mock
    private PostService postService;

    @Mock
    private RedisTemplate<String, List<Long>> redisTemplate;

    @InjectMocks
    private AuthorBanner authorBanner;

    private final String channel = "channel";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authorBanner, "channel", channel);
    }

    @Test
    void testBanUser() {
        List<Long> violatorIds = List.of(1L, 2L, 3L);
        when(postService.getAuthorsWithMoreFiveUnverifiedPosts()).thenReturn(violatorIds);

        authorBanner.banUser();

        verify(postService).getAuthorsWithMoreFiveUnverifiedPosts();
        verify(redisTemplate).convertAndSend(channel, violatorIds);
    }

    @Test
    void testBanUserWithEmptyList() {
        when(postService.getAuthorsWithMoreFiveUnverifiedPosts()).thenReturn(Collections.emptyList());

        authorBanner.banUser();

        verify(postService).getAuthorsWithMoreFiveUnverifiedPosts();
        verify(redisTemplate).convertAndSend(channel, Collections.emptyList());
    }
}
