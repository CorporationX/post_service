package faang.school.postservice.scheduled;

import faang.school.postservice.scheduler.AuthorBanner;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthorBannerTest {
    @Mock
    private PostService postService;

    @InjectMocks
    private AuthorBanner authorBanner;

    @Test
    void testGetPostsWhenVerifiedFalse() {
        authorBanner.getPostsWhenVerifiedFalse();

        verify(postService, times(1)).checkAndBanAuthors();
    }
}
