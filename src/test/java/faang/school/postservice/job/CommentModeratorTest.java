package faang.school.postservice.job;

import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentModeratorTest {
    @Mock
    CommentService commentService;

    @InjectMocks
    CommentModerator commentModerator;

    @Test
    void shouldCallModerateUnverifiedCommentsOnce(){
        commentModerator.runModerationJob();

        verify(commentService,times(1)).moderateUnverifiedComments();
        verifyNoMoreInteractions(commentService);
    }
}