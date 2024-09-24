package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.post.PostService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostService postService;

    @InjectMocks
    private CommentValidator commentValidator;

    private static final Long userId = 1L;
    private static final Long invalidUserId = 2L;
    private static final Long postId = 1L;

    @Test
    void testValidateCreate() {
        Comment comment = Comment.builder().id(postId).authorId(userId).build();
        commentValidator.validateCreate(postId, comment);
    }

    @Test
    void testValidateCreateThrowsValidationExceptionWhenExceptionInUserService() {
        Comment comment = Comment.builder().id(postId).authorId(userId).build();

        var ex = Mockito.mock(FeignException.class);

        when(userServiceClient.getUser(userId)).thenThrow(ex);

        assertThrows(
                FeignException.class,
                () -> commentValidator.validateCreate(postId, comment)
        );
    }

    @Test
    void testValidateCreateThrowsValidationExceptionWhenPostNotFound() {
        Comment comment = Comment.builder().id(postId).authorId(userId).build();

        when(postService.findPostById(postId)).thenThrow(new PostNotFoundException(postId));

        assertThrows(
                ValidationException.class,
                () -> commentValidator.validateCreate(postId, comment)
        );
    }

    @Test
    void testValidateUpdate() {
        Comment comment = Comment.builder().authorId(userId).build();
        commentValidator.validateUpdate(userId, comment);
    }

    @Test
    void testValidateUpdateThrowsValidationExceptionWhenInvalidAuthor() {
        Comment comment = Comment.builder().authorId(userId).build();
        assertThrows(
                ValidationException.class,
                () -> commentValidator.validateUpdate(invalidUserId, comment)
        );
    }
}