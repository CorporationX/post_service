package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AuthorBannerTest {

    @InjectMocks
    private AuthorBanner authorBanner;

    @Mock
    private PostService postService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testBanAuthors_Success() {
        int banPostLimit = 5;

        ReflectionTestUtils.setField(authorBanner, "banPostLimit", banPostLimit);

        authorBanner.banAuthors();

        verify(postService, times(1)).banAuthorsWithUnverifiedPostsMoreThan(banPostLimit);
    }
}