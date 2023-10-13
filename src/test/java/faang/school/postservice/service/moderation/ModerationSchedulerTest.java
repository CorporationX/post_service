package faang.school.postservice.service.moderation;

import faang.school.postservice.scheduler.ScheduledPostModeration;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModerationSchedulerTest {
    @Mock
    private PostService postService;

    @InjectMocks
    private ScheduledPostModeration moderationScheduler;

    @Test
    public void testWhenModeratePosts_thenCorrect() {
        moderationScheduler.moderatePosts();
        verify(postService, times(1)).moderatePosts();
    }
}