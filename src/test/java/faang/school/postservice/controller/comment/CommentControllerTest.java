package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.ChangeCommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    private CreateCommentDto createCommentDto;
    private ChangeCommentDto changeCommentDto;
    private Long id;

    @BeforeEach
    public void setUp() {
        createCommentDto = CreateCommentDto.builder().id(1L).content("content").authorId(1L).postId(1L).build();
        changeCommentDto = ChangeCommentDto.builder().id(1L).content("content").build();
        id = 1L;
    }

    @Test
    public void testCorrectWorkCreateComment() {
        assertDoesNotThrow(() -> commentController.createComment(createCommentDto));
        verify(commentService).createComment(createCommentDto);
    }

    @Test
    public void testCorrectWorkChangeComment() {
        assertDoesNotThrow(() -> commentController.changeComment(changeCommentDto));
        verify(commentService).changeComment(changeCommentDto);
    }

    @Test
    public void testCorrectWorkGetAllCommentsOnPostId() {
        assertDoesNotThrow(() -> commentController.getAllCommentsOnPostId(id));
        verify(commentService).getAllCommentsOnPostId(id);
    }

    @Test
    public void testCorrectWorkDeleteComment() {
        assertDoesNotThrow(() -> commentController.deleteComment(id));
        verify(commentService).deleteComment(id);
    }
}
