package faang.school.postservice.scheduler;

import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentModeratorTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentModerator moderator;

    @Test
    void testModerateCommentaries(){
        moderator.moderateCommentaries();

        verify(commentService).moderateComments();
    }
}
