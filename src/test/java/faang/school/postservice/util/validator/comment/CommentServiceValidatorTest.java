package faang.school.postservice.util.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.util.exception.DataValidationException;
import faang.school.postservice.util.exception.NotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.util.validator.CommentServiceValidator;
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
        userDto = UserDto.builder().id(1L).username("username").email("email@mail.ru").build();
        commentDto = CommentDto.builder().id(1L).authorId(1L).content("content").postId(1L).build();
    }

    @Test
    public void testValidateExistingUserValid() {
        Mockito.when(userServiceClient.getUser(Mockito.anyLong()))
                .thenReturn(userDto);
        assertDoesNotThrow(() -> validator.validateExistingUserAtCommentDto(commentDto));
    }

    @Test
    public void testValidateExistingUserInvalid() {
        Mockito.when(userServiceClient.getUser(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Author with 1 id was not found!"));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> validator.validateExistingUserAtCommentDto(commentDto));
        assertEquals("Author with " + commentDto.getAuthorId() + " id was not found!", exception.getMessage());
    }

    @Test
    public void testValidateUpdateCommentInvalidAuthorId() {
        Comment comment = Comment.builder().post(Post.builder().id(commentDto.getPostId()).build())
                .authorId(commentDto.getAuthorId() + 1).build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateUpdateComment(comment, commentDto));
        assertEquals("You can't change post and author data when editing a comment!", exception.getMessage());
    }

    @Test
    public void testValidateUpdateCommentInvalidPostId() {
        Comment comment = Comment.builder().post(Post.builder().id(commentDto.getPostId() + 1).build())
                .authorId(commentDto.getAuthorId()).build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateUpdateComment(comment, commentDto));
        assertEquals("You can't change post and author data when editing a comment!", exception.getMessage());
    }

    @Test
    public void testValidateUpdateCommentValid() {
        Comment comment = Comment.builder().post(Post.builder().id(commentDto.getPostId()).build())
                .authorId(commentDto.getAuthorId()).content("new content...").build();

        assertDoesNotThrow(() -> validator.validateUpdateComment(comment, commentDto));
    }
}