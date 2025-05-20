package faang.school.postservice.service;

import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Spy
    private Utils utils;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void findCommentByIdSuccess() {
        Long commentId = 10L;
        Comment mockComment = Comment.builder()
                .id(commentId)
                .content("mock comment")
                .build();
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(mockComment));

        Comment actualComment = commentService.findCommentById(commentId);
        assertNotNull(actualComment);
        assertNotNull(mockComment);
        assertEquals(mockComment.getId(), actualComment.getId());
    }

    @Test
    public void findCommentByIdFail() {
        Long commentId = 10L;
        String expected = utils.format(CommentService.COMMENT_BY_ID_NOT_FOUND, commentId);
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        CommentNotFoundException result = assertThrows(
                CommentNotFoundException.class, () -> commentService.findCommentById(commentId));
        assertEquals(expected, result.getMessage());
    }
}