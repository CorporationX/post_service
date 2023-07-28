package faang.school.postservice.util.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentServiceValidator validator;

    private CommentDto commentDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDto = new UserDto(1L, "username", "email@mail.ru");
        commentDto = CommentDto.builder().id(1L).authorId(1L).content("content").postId(1L).build();
    }

    @Test
    public void testValid() {
        Mockito.when(userServiceClient.getUser(Mockito.anyLong()))
                .thenReturn(userDto);
        assertDoesNotThrow(() -> validator.validateExistingUserAtCommentDto(commentDto));
    }

    @Test
    public void testInvalid() {
        Mockito.when(userServiceClient.getUser(Mockito.anyLong()))
                .thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> validator.validateExistingUserAtCommentDto(commentDto));
        assertEquals("Author with " + commentDto.getAuthorId() + " id was not found!", exception.getMessage());
    }
}