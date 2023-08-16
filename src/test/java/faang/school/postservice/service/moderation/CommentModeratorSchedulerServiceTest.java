package faang.school.postservice.service.moderation;

import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CommentModeratorSchedulerServiceTest {

    @Mock
    CommentService commentService;

    @Mock
    ModerationDictionary moderationDictionary;

    @InjectMocks
    CommentModeratorSchedulerService commentModeratorService;

    @Test
    public void testModerateComments() {
        commentModeratorService.setBatchSize(50);
        Mockito.when(commentService.findUnverifiedComments()).thenReturn(List.of(new Comment()));
        commentModeratorService.moderateComments();

        Mockito.verify(commentService).findUnverifiedComments();
        Mockito.verify(moderationDictionary).checkComment(Mockito.any());
        Mockito.verify(commentService).saveAll(Mockito.any());
    }
}