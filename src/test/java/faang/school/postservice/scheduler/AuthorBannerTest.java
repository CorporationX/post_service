package faang.school.postservice.scheduler;

import faang.school.postservice.publis.publisher.UserBanMessagePublisher;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorBannerTest {
    @Mock
    private PostService postService;
    @Mock
    private UserBanMessagePublisher redisMessagePublisher;
    @InjectMocks
    private AuthorBanner authorBanner;

    @Test
    public void testBanAuthorsSuccess() {
        List<Long> banAuthorsIds = List.of(1L, 2L, 3L);

        when(postService.getAuthorsWithExcessVerifiedFalsePosts()).thenReturn(banAuthorsIds);

        authorBanner.sendBanAuthorsIdsToPublisher();

        verify(postService, atLeastOnce()).getAuthorsWithExcessVerifiedFalsePosts();
        verify(redisMessagePublisher, atLeastOnce()).publish(banAuthorsIds.toString());
    }
}
