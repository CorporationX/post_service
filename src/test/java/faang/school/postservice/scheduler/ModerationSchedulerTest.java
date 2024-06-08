package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModerationSchedulerTest {
    @Mock
    private PostServiceImpl postService;

    @InjectMocks
    private ModerationScheduler moderationScheduler;

    @Test
    public void whenModeratePostsSuccessfully() {
        moderationScheduler.moderatePosts();
        verify(postService, times(1)).moderatePosts();
    }
}