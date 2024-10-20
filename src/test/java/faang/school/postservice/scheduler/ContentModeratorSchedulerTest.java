package faang.school.postservice.scheduler;

import faang.school.postservice.service.moderator.ModeratorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentModeratorSchedulerTest {

    @InjectMocks
    private ContentModeratorScheduler contentModeratorScheduler;

    @Mock
    private ModeratorService moderatorService;

    @Test
    @DisplayName("Should call moderate comments content method")
    void whenInitThenShouldCallModerateCommentsContent() {
        /*when(moderatorService.moderateCommentsContent())
                .thenReturn(CompletableFuture.completedFuture(null));
*/
        contentModeratorScheduler.moderateComments();

        verify(moderatorService).moderateCommentsContent();
    }
}