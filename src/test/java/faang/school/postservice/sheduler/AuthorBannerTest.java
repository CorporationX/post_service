package faang.school.postservice.sheduler;

import faang.school.postservice.publisher.AuthorBannerPublisher;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
//@TestPropertySource(properties = {"author_banner.start_scheduler_cron=0 0 0 * * *"})
class AuthorBannerTest {

    @Mock
    private PostService postService;
    @Mock
    private AuthorBannerPublisher authorBannerPublisher;
    @InjectMocks
    private AuthorBanner authorBanner;

    @Test
    void testCheckVerified() {
        Mockito.when(postService.getByPostIsVerifiedFalse()).thenReturn(List.of(1L));

        authorBanner.checkVerified();

        Mockito.verify(authorBannerPublisher).publish(List.of(1L));
    }

    @Test
    void testCheckVerifiedWhenEmptyList() {
        Mockito.when(postService.getByPostIsVerifiedFalse()).thenReturn(List.of());

        authorBanner.checkVerified();

        Mockito.verify(authorBannerPublisher, Mockito.times(0)).publish(List.of(1L));
    }
}