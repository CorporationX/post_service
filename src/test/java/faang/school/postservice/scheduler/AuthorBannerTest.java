package faang.school.postservice.scheduler;

import faang.school.postservice.exception.comment.UserBanException;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
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
        authorBanner.banAuthors();

        verify(postService, times(1)).banAuthorsWithUnverifiedPostsMoreThan();
    }

    @Test
    void testBanAuthors_WithException() {
        doThrow(new UserBanException("Failed to ban authors", new RuntimeException("Redis down")))
                .when(postService).banAuthorsWithUnverifiedPostsMoreThan();

        UserBanException exception = assertThrows(UserBanException.class, () -> {
            authorBanner.banAuthors();
        });

        assertEquals("Failed to ban authors", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Redis down", exception.getCause().getMessage());

        verify(postService, times(1)).banAuthorsWithUnverifiedPostsMoreThan();
    }
}