package faang.school.postservice.service.moderator;

import faang.school.postservice.config.dictionary.OffensiveWordsDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModeratorServiceTest {

    private static final int TWO_TIMES_USED = 2;

    @InjectMocks
    private ModeratorService moderatorService;

    @Mock
    private CommentService commentService;

    @Mock
    private ExecutorService executorService;

    @Mock
    private OffensiveWordsDictionary offensiveWordsDictionary;

/*    @Test
    void test() {
        Comment comment = Comment.builder()
                .content("test test")
                .build();

        Comment comment1 = Comment.builder()
                .content("a b")
                .build();
        List<Comment> unverifiedComments = Arrays.asList(comment, comment1);

        when(commentService.getUnverifiedComments())
                .thenReturn(unverifiedComments);
        doNothing().when(commentService).setVerifyToComment(any(Comment.class), anyBoolean());

        moderatorService.moderateCommentsContent().join();

        verify(commentService).getUnverifiedComments();

        verify(commentService).setVerifyToComment(comment, anyBoolean());
        verify(commentService).setVerifyToComment(comment1, anyBoolean());

        verify(executorService, times(TWO_TIMES_USED)).execute(any(Runnable.class));
    }*/
}
