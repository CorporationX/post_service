package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {
    @InjectMocks
    CommentValidator commentValidator;

    @Mock
    UserServiceClient userServiceClient;

    @Mock
    CommentRepository commentRepository;

    private Comment comment;
    private Post post;
    private CommentDto commentDto;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto(1L, "username", "email");

        post = new Post();
        post.setId(1);
        post.setContent("test");

        commentDto = CommentDto.builder()
                .id(1L)
                .content("Test content")
                .authorName("NameTest")
                .authorId(1L)
                .postId(1L).build();

        comment = Comment.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .post(post)
                .build();
    }

    @Test
    public void testGetComment_ShouldReturnComment() {
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));

        Comment result = commentValidator.getComment(commentDto);

        assertEquals(comment, result);
    }

    @Test
    public void testGetComment_ShouldThrowException_WhenCommentNotFound() {
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> commentValidator.getComment(commentDto));
    }

    @Test
    public void testExistsAuthor_ShouldNotThrowException_WhenUserIsNotNull() {
        assertDoesNotThrow(() -> commentValidator.existsAuthor(userDto));
    }

    @Test
    public void testExistsAuthor_ShouldThrowException_WhenUserIsNull() {
        assertThrows(ValidationException.class, () -> commentValidator.existsAuthor(null));
    }

    @Test
    public void testValidateAuthorIdUpdateComment_ShouldNotThrowException_WhenAuthorIdIsValid() {
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> commentValidator.validateAuthorIdUpdateComment(commentDto));
    }

    @Test
    public void testValidateAuthorIdUpdateComment_ShouldThrowException_WhenAuthorIdIsInvalid() {
        commentDto.setAuthorId(2L);

        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));

        assertThrows(ValidationException.class, () -> commentValidator.validateAuthorIdUpdateComment(commentDto));
    }

    @Test
    public void testValidatePostIdUpdateComment_ShouldNotThrowException_WhenPostIdIsValid() {
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> commentValidator.validatePostIdUpdateComment(commentDto));
    }

    @Test
    public void testValidatePostIdUpdateComment_ShouldThrowException_WhenPostIdIsInvalid() {
        commentDto.setPostId(2L);

        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));

        assertThrows(ValidationException.class, () -> commentValidator.validatePostIdUpdateComment(commentDto));
    }

    @Test
    public void testValidateAuthorNameUpdateComment_ShouldThrowException_WhenAuthorNameIsInvalid() {
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(new UserDto(1L, "wrongname", "email"));

        assertThrows(ValidationException.class, () -> commentValidator.validateAuthorNameUpdateComment(commentDto));
    }

    @Test
    public void testValidateCommentIdUpdateComment_ShouldNotThrowException_WhenCommentIdIsValid() {
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> commentValidator.validateCommentIdUpdateComment(commentDto));
    }

    @Test
    public void testValidateCommentIdUpdateComment_ShouldThrowException_WhenCommentIdIsInvalid() {
        commentDto.setId(2L);

        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));

        assertThrows(ValidationException.class, () -> commentValidator.validateCommentIdUpdateComment(commentDto));
    }

    @Test
    public void testValidateAuthorDeleteComment_ShouldNotThrowException_WhenAuthorIsValid() {
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> commentValidator.validateAuthorDeleteComment(commentDto));
    }

    @Test
    public void testValidateAuthorDeleteComment_ShouldThrowException_WhenAuthorIsInvalid() {
        commentDto.setAuthorId(2L);

        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));

        assertThrows(ValidationException.class, () -> commentValidator.validateAuthorDeleteComment(commentDto));
    }
}
