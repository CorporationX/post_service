package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private CommentValidator commentValidator;

    private CommentDto commentDto;
    private UserDto userDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        commentDto = CommentDto.builder()
                .authorId(1L)
                .postId(100L)
                .build();

        userDto = UserDto.builder().id(1L).build();

        comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .post(Post.builder().id(100L).build())
                .build();
    }

    @Test
    void testValidateUserBeforeCreate_ValidData_NoExceptions() {

        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(userDto);

        assertDoesNotThrow(() -> commentValidator.validateUserBeforeCreate(commentDto));
    }

    @Test
    void testValidateUserBeforeCreate_InvalidAuthorId_ThrowsEntityNotFoundException() {

        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> commentValidator.validateUserBeforeCreate(commentDto));
    }

    @Test
    void testValidateBeforeUpdate_ValidData_NoExceptions() {

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorId(1L)
                .postId(100L)
                .build();

        assertDoesNotThrow(() -> commentValidator.validateBeforeUpdate(comment, commentDto));
    }

    @Test
    void testValidateBeforeUpdate_ChangeAuthor_ThrowsDataValidationException() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorId(2L) // Changing the authorId
                .postId(100L)
                .build();

        assertThrows(DataValidationException.class, () -> commentValidator.validateBeforeUpdate(comment, commentDto));
    }

    @Test
    void testValidateBeforeUpdate_ChangePost_ThrowsDataValidationException() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorId(1L)
                .postId(200L) // Changing the postId
                .build();

        assertThrows(DataValidationException.class, () -> commentValidator.validateBeforeUpdate(comment, commentDto));
    }
}