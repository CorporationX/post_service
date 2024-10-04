package faang.school.postservice.moderation;

import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModerationSchedulerTest {

    @InjectMocks
    private ModerationScheduler moderationScheduler;
    @Mock
    private PostService postService;

    @Test
    @DisplayName("Успешный вызов метода moderationContent")
    public void whenModerationContentSuccess() {
        moderationScheduler.moderationContent();

        verify(postService).moderationPostContent();
    }
}