package faang.school.postservice.service.comment;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundEntityException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void validationAndCommentsReceived_ShouldReturnComment_WhenValidCommentIdIsProvided() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);

        LikeDto likeDto = LikeDto.builder().commentId(commentId).build();

        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentService.validationAndCommentsReceived(likeDto);

        assertEquals(comment, result);
    }

    @Test
    void validationAndCommentsReceived_ShouldThrowDataValidationExceptions_WhenCommentIdIsNull() {
        LikeDto likeDto = LikeDto.builder().commentId(null).build();

        assertThrows(DataValidationException.class, () ->
                commentService.validationAndCommentsReceived(likeDto));
    }

    @Test
    void validationAndCommentsReceived_ShouldThrowDataValidationExceptions_WhenCommentDoesNotExist() {
        Long commentId = 1L;
        LikeDto likeDto = LikeDto.builder().commentId(commentId).build();

        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                commentService.validationAndCommentsReceived(likeDto));
    }

    @Test
    void validationAndCommentsReceived_ShouldThrowNotFoundElementException_WhenCommentNotFound() {
        Long commentId = 1L;
        LikeDto likeDto = LikeDto.builder().commentId(commentId).build();

        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () ->
                commentService.validationAndCommentsReceived(likeDto));
    }
}