package faang.school.postservice.scheduler;

import faang.school.postservice.service.comment.CommentServiceImpl;
import faang.school.postservice.publisher.UserIdsPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommenterBannerTest {

    @Mock
    private CommentServiceImpl commentService;

    @Mock
    private UserIdsPublisher userIdsPublisher;

    @InjectMocks
    private CommenterBanner commenterBanner;

    @Test
    public void testScheduleCommentersBanCheck_noUsersToBan() {
        // Given
        Map<Long, Long> unverifiedCommentAuthorsAndComments = Map.of(1L, 2L, 2L, 3L);
        when(commentService.groupUnverifiedCommentAuthors(anyList())).thenReturn(unverifiedCommentAuthorsAndComments);
        when(commentService.collectUnverifiedComments()).thenReturn(List.of());

        // When
        commenterBanner.scheduleCommentersBanCheck();

        // Then
        verify(commentService, times(1)).collectUnverifiedComments();
        verify(commentService, times(1)).groupUnverifiedCommentAuthors(anyList());
        verify(userIdsPublisher, times(1)).publish(List.of());
    }

    @Test
    public void testScheduleCommentersBanCheck_usersToBan() {
        // Given
        Map<Long, Long> unverifiedCommentAuthorsAndComments = Map.of(1L, 6L, 2L, 4L, 3L, 7L);
        when(commentService.groupUnverifiedCommentAuthors(anyList())).thenReturn(unverifiedCommentAuthorsAndComments);
        when(commentService.collectUnverifiedComments()).thenReturn(List.of());

        // When
        commenterBanner.scheduleCommentersBanCheck();

        // Then
        verify(commentService, times(1)).collectUnverifiedComments();
        verify(commentService, times(1)).groupUnverifiedCommentAuthors(anyList());
        verify(userIdsPublisher, times(1)).publish(List.of(1L, 3L));
    }

    @Test
    public void testScheduleCommentersBanCheck_noUnverifiedComments() {
        // Given
        when(commentService.collectUnverifiedComments()).thenReturn(List.of());
        when(commentService.groupUnverifiedCommentAuthors(anyList())).thenReturn(Map.of());

        // When
        commenterBanner.scheduleCommentersBanCheck();

        // Then
        verify(commentService, times(1)).collectUnverifiedComments();
        verify(commentService, times(1)).groupUnverifiedCommentAuthors(anyList());
        verify(userIdsPublisher, times(1)).publish(List.of());
    }
}
