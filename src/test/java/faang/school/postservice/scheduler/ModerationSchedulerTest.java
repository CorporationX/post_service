package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ModerationSchedulerTest {

    @Mock
    private PostService postService;
    @InjectMocks
    private ModerationScheduler moderationScheduler;

    @Test
    void testDoPostModeration() {
        moderationScheduler.doPostModeration();
        Mockito.verify(postService).doPostModeration();
    }
}
