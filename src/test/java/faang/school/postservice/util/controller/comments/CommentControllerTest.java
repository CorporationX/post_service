package faang.school.postservice.util.controller.comments;

import faang.school.postservice.controller.CommentController;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comments.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    private CommentDto inputDto;
    private CommentDto returnedDto;
    private List<CommentDto> dtoList;

    @BeforeEach
    void setUp() {
        commentController = new CommentController(commentService);

        inputDto = new CommentDto();
        inputDto.setAuthorId(1L);
        inputDto.setContent("Hello");

        returnedDto = new CommentDto();
        returnedDto.setId(42L);
        returnedDto.setAuthorId(1L);
        returnedDto.setContent("Hello");
        returnedDto.setPostId(7L);

        dtoList = Collections.singletonList(returnedDto);
    }


    @Test
    void createComment_shouldCallServiceAndReturnDto() {
        when(commentService.createComment(any(CommentDto.class))).thenReturn(returnedDto);

        CommentDto result = commentController.createComment(7L, inputDto);

        assertEquals(7L, inputDto.getPostId());
        assertEquals(returnedDto, result);
        verify(commentService, times(1)).createComment(inputDto);
    }

    @Test
    void getCommentsByPostId_shouldReturnListFromService() {
        when(commentService.getCommentsByPostId(7L)).thenReturn(dtoList);

        List<CommentDto> result = commentController.getCommentsByPostId(7L);

        assertEquals(dtoList, result);
        verify(commentService, times(1)).getCommentsByPostId(7L);
    }

    @Test
    void updateComment_shouldCallServiceAndReturnDto() {
        // assume, that DTO already filled with required content
        when(commentService.updateComment(eq(42L), any(CommentDto.class)))
                .thenReturn(returnedDto);

        CommentDto result = commentController.updateComment(42L, inputDto);

        assertEquals(returnedDto, result);
        verify(commentService, times(1)).updateComment(42L, inputDto);
    }

    @Test
    void deleteComment_shouldCallService() {
        assertDoesNotThrow(() -> commentController.deleteComment(42L));
        verify(commentService, times(1)).deleteComment(42L);
    }

}
