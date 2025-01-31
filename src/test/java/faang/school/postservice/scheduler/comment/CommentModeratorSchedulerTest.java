package faang.school.postservice.scheduler.comment;

import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentModeratorSchedulerTest {
    @Mock
    CommentService commentService;
    @InjectMocks
    CommentModeratorScheduler commentModeratorScheduler;

    @Test
    void testModerateCommentsToOffensiveContent() {
        commentModeratorScheduler.moderateCommentsToOffensiveContent();
        verify(commentService).checkProfanities();
    }
}