package faang.school.postservice.service.comment.async;

import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.impl.comment.async.CommentServiceAsyncImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class CommentServiceAsyncImplTest {

    @Mock
    private ModerationDictionary dictionary;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceAsyncImpl commentServiceAsync;

    @Test
    void testModerateCommentsByBatches(){
        Comment comment1 = Comment.builder()
                .content("first")
                .verified(null)
                .build();
        Comment comment2 = Comment.builder()
                .content("second")
                .verified(null)
                .build();

        List<Comment> comments = List.of(comment1, comment2);

        when(dictionary.containsBadWords(comments.get(0).getContent())).thenReturn(false);
        when(dictionary.containsBadWords(comments.get(1).getContent())).thenReturn(true);

        commentServiceAsync.moderateCommentsByBatches(comments);

        assertTrue(comments.get(0).getVerified());
        assertFalse(comments.get(1).getVerified());
        verify(commentRepository).saveAll(any());
    }
}
