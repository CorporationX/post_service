package faang.school.postservice.scheduler;

import faang.school.postservice.service.ModerationPostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ModerationSchedulerTest {

    @Mock
    private ModerationPostService moderationPostService;

    @InjectMocks
    private ModerationScheduler moderationScheduler;

    @Test
    public void testModerationPosts() {
        moderationScheduler.moderationPosts();

        verify(moderationPostService).moderationPosts();
    }

    @Test
    public void testModerationPostsWithRuntimeException() {
        String message = "Test Exception";
        doThrow(new RuntimeException(message)).when(moderationPostService).moderationPosts();

        Throwable exception = assertThrows(RuntimeException.class,
                () -> moderationScheduler.moderationPosts());

        verify(moderationPostService).moderationPosts();
        assertEquals(message, exception.getMessage());
    }
}