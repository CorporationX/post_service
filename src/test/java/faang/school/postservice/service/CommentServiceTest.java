package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ModerationDictionary moderationDictionary;
    @InjectMocks
    private CommentService commentService;

    @Test
    void testFindExistingComment_ExistingId() {
        long commentId = 1;
        Comment comment = new Comment();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentService.findExistingComment(commentId);

        assertNotNull(result);
        assertEquals(comment, result);
        verify(commentRepository).findById(commentId);
    }

    @Test
    void testFindExistingComment_NonExistingId() {
        long commentId = 1;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            commentService.findExistingComment(commentId);
        });
        verify(commentRepository).findById(commentId);
    }

    @Test
    void testGetUnverifiedComments() {
        List<Comment> unverifiedComments = new ArrayList<>();
        when(commentRepository.findByVerifiedDateBeforeAndVerifiedFalse(any(LocalDateTime.class)))
                .thenReturn(unverifiedComments);

        List<Comment> result = commentService.getUnverifiedComments();

        assertNotNull(result);
        assertEquals(unverifiedComments, result);
        verify(commentRepository).findByVerifiedDateBeforeAndVerifiedFalse(any(LocalDateTime.class));
    }

    @Test
    void testProcessCommentsBatch_ContainsBannedWords() {
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setContent("Comment with word - horrifying");
        Comment comment2 = new Comment();
        comment2.setContent("Comment with word - shocking");
        comments.add(comment1);
        comments.add(comment2);

        when(moderationDictionary.containsBannedWord(anyString())).thenReturn(true);

        commentService.processCommentsBatch(comments);

        assertFalse(comment1.isVerified());
        assertFalse(comment2.isVerified());
        assertNotNull(comment1.getVerifiedDate());
        assertNotNull(comment2.getVerifiedDate());
        verify(commentRepository, times(2)).save(any(Comment.class));
    }


    @Test
    void testProcessCommentsBatch_NoBannedWords() {
        // Arrange
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setContent("Comment without banned words");
        Comment comment2 = new Comment();
        comment2.setContent("Some comment");
        comments.add(comment1);
        comments.add(comment2);

        when(moderationDictionary.containsBannedWord(anyString())).thenReturn(false);

        commentService.processCommentsBatch(comments);

        assertTrue(comment1.isVerified());
        assertTrue(comment2.isVerified());
        assertNotNull(comment1.getVerifiedDate());
        assertNotNull(comment2.getVerifiedDate());
        verify(commentRepository, times(2)).save(any(Comment.class));
    }
}
