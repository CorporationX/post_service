package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @Spy
    private TestData testData;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Captor
    private ArgumentCaptor<CommentDto> commentDtoArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> postIdCapture;

    private CommentDto commentDto;

    private long postId;

    @BeforeEach
    void init() {
        commentDto = commentMapper.ToDto(testData.returnComment());
        postId = 1L;
    }

    @Test
    void testForCreateComment() {
        commentController.createComment(postId, commentDto);

        verify(commentService).createComment(postIdCapture.capture(), commentDtoArgumentCaptor.capture());
        assertEquals(commentDto.getContent(), commentDtoArgumentCaptor.getValue().getContent());
        assertEquals(postId, postIdCapture.getValue());
    }

    @Test
    void testForUpdateComment() {
        commentController.updateComment(commentDto);

        verify(commentService).updateComment(commentDtoArgumentCaptor.capture());
        assertEquals(commentDto.getContent(), commentDtoArgumentCaptor.getValue().getContent());
    }

    @Test
    void testForGetAllComments() {
        commentController.getAllComments(postId);

        verify(commentService).getAllComments(postIdCapture.capture());
        assertEquals(postId, postIdCapture.getValue());
    }

    @Test
    void testForDeleteComment() {
        commentController.deleteComment(commentDto);

        verify(commentService).deleteComment(commentDtoArgumentCaptor.capture());
        assertEquals(commentDto.getContent(), commentDtoArgumentCaptor.getValue().getContent());
    }
}
