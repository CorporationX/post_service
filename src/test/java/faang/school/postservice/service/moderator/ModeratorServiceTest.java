package faang.school.postservice.service.moderator;

import faang.school.postservice.config.dictionary.OffensiveWordsDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModeratorServiceTest {

    private static final int TWO_TIMES_USED = 4;

    @Mock
    private CommentService commentService;

    @Mock
    private OffensiveWordsDictionary offensiveWordsDictionary;

    private ModeratorService moderatorService;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Comment comment;
    private Comment comment1;
    private List<Comment> unverifiedComments;

    @BeforeEach
    void init() {
        moderatorService = new ModeratorService(commentService, executorService, offensiveWordsDictionary);

    }

/*
    @Test
    @DisplayName("When no offensive words then update comment and save")
    void whenNoOffensiveWordsThenUpdateAndSaveComments() {
        comment = Comment.builder()
                .content("test test1")
                .build();

        comment1 = Comment.builder()
                .content("a b")
                .build();

        unverifiedComments = Arrays.asList(comment, comment1);
        when(commentService.getUnverifiedComments())
                .thenReturn(unverifiedComments);
        when(offensiveWordsDictionary.isWordContainsInDictionary(anyString()))
                .thenReturn(Boolean.FALSE);

        moderatorService.moderateCommentsContent();

        verify(commentService).getUnverifiedComments();
        // verify(offensiveWordsDictionary, times(TWO_TIMES_USED)).isWordContainsInDictionary(anyString());
        verify(commentService).saveComments(anyList());

        assertEquals(Boolean.TRUE, comment.isVerified());
        assertEquals(Boolean.TRUE, comment1.isVerified());
    }
*/

/*    @Test
    @DisplayName("When offensive words contains then update comment and save")
    void whenOffensiveWordsThenUpdateAndSaveComments() {
        comment = Comment.builder()
                .content("test test1")
                .build();

        comment1 = Comment.builder()
                .content("a b")
                .build();

        unverifiedComments = Arrays.asList(comment, comment1);
        when(commentService.getUnverifiedComments())
                .thenReturn(unverifiedComments);
        when(offensiveWordsDictionary.isWordContainsInDictionary(anyString()))
                .thenReturn(Boolean.TRUE);

        moderatorService.moderateCommentsContent();

        verify(commentService).getUnverifiedComments();
        //  verify(offensiveWordsDictionary, times(TWO_TIMES_USED)).isWordContainsInDictionary(anyString());
        verify(commentService).saveComments(anyList());

        assertEquals(Boolean.FALSE, comment.isVerified());
        assertEquals(Boolean.FALSE, comment1.isVerified());
    }*/
}
