package faang.school.postservice.moderator.comment.logic;

import faang.school.postservice.model.Comment;
import faang.school.postservice.moderator.comment.dictionary.ModerationDictionary;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentModeratorTest {
    @InjectMocks
    private CommentModerator commentModerator;

    @Mock
    private ModerationDictionary moderationDictionary;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private EntityManager entityManager;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Captor
    private ArgumentCaptor<Boolean> verifiedCaptor;

    @Captor
    private ArgumentCaptor<LocalDateTime> verifiedDateCaptor;

    private List<Comment> comments;

    @BeforeEach
    public void setUp() {
        Comment comment1 = Comment.builder().id(1L).content("This is a clean comment").authorId(1L).build();
        Comment comment2 = Comment.builder().id(2L).content("This is a bad comment with badword").authorId(1L).build();
        comments = Arrays.asList(comment1, comment2);
    }

    @Test
    public void testModerateComment() {
        when(moderationDictionary.isContainsBadWordInTheText("This is a clean comment")).thenReturn(false);
        when(moderationDictionary.isContainsBadWordInTheText("This is a bad comment with badword")).thenReturn(true);

        commentModerator.moderateComment(comments);

        verify(commentRepository, times(2)).updateVerifiedAndVerifiedDate(idCaptor.capture(), verifiedCaptor.capture(), verifiedDateCaptor.capture());

        List<Long> capturedIds = idCaptor.getAllValues();
        List<Boolean> capturedVerifieds = verifiedCaptor.getAllValues();
        List<LocalDateTime> capturedVerifiedDates = verifiedDateCaptor.getAllValues();

        assertEquals(1L, capturedIds.get(0));
        assertTrue(capturedVerifieds.get(0));
        assertEquals(2L, capturedIds.get(1));
        assertFalse(capturedVerifieds.get(1));

        LocalDateTime now = LocalDateTime.now();
        assertTrue(capturedVerifiedDates.get(0).isBefore(now.plusSeconds(1)));
        assertTrue(capturedVerifiedDates.get(1).isBefore(now.plusSeconds(1)));

        verify(entityManager, times(1)).clear();
    }
}
