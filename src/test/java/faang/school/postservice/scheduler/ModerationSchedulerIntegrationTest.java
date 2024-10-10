package faang.school.postservice.scheduler;

import faang.school.postservice.service.ModerationPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
public class ModerationSchedulerIntegrationTest {

    @Autowired
    private ModerationScheduler moderationScheduler;

    @SpyBean
    private ModerationPostService moderationPostService;

    @Test
    public void testModerationPosts() {
        moderationScheduler.moderationPosts();

        verify(moderationPostService).moderationPosts();
    }

    @Test
    public void testModerationPostsWithRetry() {
        String message = "Test Exception";
        doThrow(new RuntimeException(message)).when(moderationPostService).moderationPosts();

        Throwable exception = assertThrows(RuntimeException.class,
                () -> moderationScheduler.moderationPosts());

        verify(moderationPostService, times(3)).moderationPosts();
        assertEquals(message, exception.getMessage());
    }
}
