package faang.school.postservice.service;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    private CommentService commentService;

    @Value("${comment.moderator.count-comments-in-thread}")
    private int countPostsInThread;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        commentService = new CommentService(commentRepository, moderationDictionary);
        commentService.countPostsInThread = 2;
    }

    @Test
    public void testModerateComments_NoComments() {
        when(commentRepository.findNotVerified()).thenReturn(Collections.emptyList());
        commentService.moderateComments();
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void testModerateComments_WithComments() {
        List<Comment> comments = Arrays.asList(Comment.builder().id(1L).content("This is a test comment").verified(false).build(),
                Comment.builder().id(2L).content("This is a badword comment").verified(false).build()

        );
        when(commentRepository.findNotVerified()).thenReturn(comments);
        when(moderationDictionary.getBadWords()).thenReturn(new HashSet<>(List.of("badword")));

        commentService.moderateComments();

    }

    @Test
    public void testVerifyComments_NoBadWords() {
        List<Comment> comments = Arrays.asList(
                Comment.builder().id(1L).content("This is a test comment").verified(false).build()
        );
        when(moderationDictionary.getBadWords()).thenReturn(new HashSet<>(Arrays.asList("badword")));

        commentService.verifyComments(comments);

        assertTrue(comments.get(0).isVerified());
        assertNotNull(comments.get(0).getVerifiedDate());
        verify(commentRepository, times(1)).save(comments.get(0));
    }

    @Test
    public void testVerifyComments_WithBadWords() {
        List<Comment> comments = Arrays.asList(
                Comment.builder().id(1L).content("This is a badword comment").verified(false).build()

        );
        when(moderationDictionary.getBadWords()).thenReturn(new HashSet<>(Arrays.asList("badword")));

        commentService.verifyComments(comments);

        assertFalse(comments.get(0).isVerified());
        assertNull(comments.get(0).getVerifiedDate());
        verify(commentRepository, times(1)).save(comments.get(0));
    }
}