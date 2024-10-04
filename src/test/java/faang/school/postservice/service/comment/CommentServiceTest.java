package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Test
    @DisplayName("Method returned comments list")
    void whenCallFindByVerifiedFalseThanReturnListComments() {
        when(commentRepository.findByVerifiedAtIsNull())
                .thenReturn(new ArrayList<>());

        commentService.getUnverifiedComments();

        verify(commentRepository).findByVerifiedAtIsNull();
    }

    @Test
    @DisplayName("Method should update comment and save it")
    void whenCallVerifyThanUpdateCommentAndSave() {
        Comment comment = new Comment();

        commentService.verify(comment, Boolean.TRUE);

        assertTrue(comment.isVerified());
        assertNotNull(comment.getVerifiedAt());
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("Method should save comment")
    void whenCallSaveCommentThanSaveComment() {
        commentService.saveComment(any());

        verify(commentRepository).save(any());
    }
}