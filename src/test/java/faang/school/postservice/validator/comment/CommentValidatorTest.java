package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.comment.DataValidationException;
import faang.school.postservice.validation.comment.CommentValidator;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {

    @InjectMocks
    private CommentValidator commentValidator;

    @Mock
    private UserServiceClient userServiceClient;

    private CommentDto commentDto;

    private UserDto userDto;


    @BeforeEach
    public void init() {
        commentDto = CommentDto.builder()
                .authorId(1L)
                .id(2L)
                .content("Content")
                .postId(3L)
                .build();
        userDto = UserDto.builder()
                .email("Email")
                .username("Username")
                .build();
    }

    @Test
    public void testExceptionForEmptyCommentData() {
        Assert.assertThrows(DataValidationException.class, () ->
                commentValidator.validateCommentData(new CommentDto()));
    }

    @Test
    public void testExceptionForEmptyAuthorData() {
        Mockito.when(userServiceClient.getUser(anyLong())).thenReturn(userDto);
        Assert.assertThrows(DataValidationException.class, () ->
                commentValidator.validateCommentAuthor(commentDto));
    }

    @Test
    public void testExceptionFromUserService() {
        Mockito.when(userServiceClient.getUser(anyLong())).thenReturn(null);
        Assert.assertThrows(DataValidationException.class, () ->
                commentValidator.validateCommentAuthor(commentDto));
    }

    @Test
    public void testValidateCommentData() {
        try {
            commentValidator.validateCommentData(commentDto);
        } catch (DataValidationException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    public void testValidateAuthorData() {
        Mockito.when(userServiceClient.getUser(anyLong())).thenReturn(userDto);
        userDto.setId(1L);
        try {
            commentValidator.validateCommentAuthor(commentDto);
        } catch (DataValidationException e) {
            fail("Should not have thrown any exception");
        }
    }
}

