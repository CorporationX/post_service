package faang.school.postservice.validation.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentValidationTest {
    private static final long POST_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long COMMENT_ID = 3L;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentValidation commentValidation;

    Comment comment;
    Comment updateComment;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        updateComment = new Comment();
    }

    @Test
    void testValidateLengthContentCommentWhenContentEmpty() {
        comment.setContent("");
        assertThrows(DataValidationException.class,
                () -> commentValidation.validateLengthContentComment(comment));
    }

    @Test
    void testValidateLengthContentCommentWhenContentExists() {
        comment.setContent("Comment");
        assertDoesNotThrow(() -> commentValidation.validateLengthContentComment(comment));
    }

    @Test
    void testValidateLengthContentCommentWhenContentOverLength() {
        String content = "c".repeat(CommentValidation.MAX_LENGTH_CONTENT + 1);
        comment.setContent(content);

        assertThrows(DataValidationException.class,
                () -> commentValidation.validateLengthContentComment(comment));
    }

    @Test
    void testValidateAuthorExistsWhenAuthorExists() {
        comment.setAuthorId(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(any());

        assertDoesNotThrow(() -> commentValidation.validateAuthorExists(comment));
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    void testValidateAuthorExistsWhenAuthorNoExists() {
        comment.setAuthorId(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenThrow(new DataValidationException("User not Exists"));

        assertThrows(DataValidationException.class,
                () -> commentValidation.validateAuthorExists(comment));
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    void testValidateCommentEqualsUpdateCommentWhenEqual() {
        comment.setContent("Comment");
        updateComment.setContent("Comment");

        assertThrows(DataValidationException.class,
                () -> commentValidation.validateCommentEqualsUpdateComment(comment, updateComment));
    }

    @Test
    void testValidateCommentEqualsUpdateCommentWhenNoEqual() {
        comment.setContent("CommentOne");
        updateComment.setContent("CommentTwo");

        assertDoesNotThrow(
                () -> commentValidation.validateCommentEqualsUpdateComment(comment, updateComment));
    }


    @Test
    void testValidatePostExistsWhenPostExists() {
        Post post = new Post();
        post.setId(POST_ID);
        comment.setPost(post);

        when(postRepository.existsById(POST_ID)).thenReturn(true);

        assertDoesNotThrow(() -> commentValidation.validatePostExists(comment));
        verify(postRepository).existsById(POST_ID);
    }

    @Test
    void testValidatePostExistsWhenPostNoExists() {
        Post post = new Post();
        post.setId(POST_ID);
        comment.setPost(post);

        when(postRepository.existsById(POST_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> commentValidation.validatePostExists(comment));
        verify(postRepository).existsById(POST_ID);
    }

    @Test
    void testValidatePostExistsWhenPostIsNull() {
        assertThrows(DataValidationException.class,
                () -> commentValidation.validatePostExists(comment));
    }

    @Test
    void testValidateCommentExistsWhenCommentNoExists() {
        when(commentRepository.existsById(COMMENT_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> commentValidation.validateCommentExists(COMMENT_ID));
    }
}