package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.List;

class CommentValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private CommentValidator commentValidator;

    private long rightId;
    private long wrongId;
    private UserDto userDto;
    private Comment comment = new Comment();
    Post post = new Post();
    List<Comment> comments = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        rightId = 1L;
        wrongId = -2L;
        comment.setAuthorId(rightId);
        comment.setId(rightId);
        userDto = new UserDto(rightId, "any", "any", List.of(), List.of());

        Mockito.when(postService.getPostById(rightId))
                .thenReturn(new Post());
        Mockito.when(userServiceClient.getUser(rightId))
                .thenReturn(userDto);
        Mockito.when(commentService.getCommentById(rightId))
                .thenReturn(comment);
    }

    @Test
    void testIdValidator() {
        assertDoesNotThrow(() -> commentValidator.validateId(rightId));
        assertThrows(DataValidationException.class,
                () -> commentValidator.validateId(wrongId));
    }

    @Test
    void testCommentDtoValidator() {
        CommentDto commentDto = new CommentDto(rightId, rightId, rightId, "any content", LocalDateTime.now(),LocalDateTime.now());
        assertDoesNotThrow(() -> commentValidator.validateCommentDto(commentDto));

        commentDto.setContent("");
        assertThrows(DataValidationException.class,
                () -> commentValidator.validateCommentDto(commentDto));
    }

    @Test
    void testPostExistValidator() {
        postService.getPostById(rightId);
        Mockito.verify(postService, Mockito.times(1))
                .getPostById(rightId);
    }

    @Test
    void testAuthorExistValidator() {
        assertDoesNotThrow(() -> commentValidator.validateAuthorExist(rightId));
        assertThrows(DataValidationException.class,
                () -> commentValidator.validateAuthorExist(wrongId));
    }

    @Test
    void testUpdateCommentValidator() {
        post.setComments(comments);

        assertThrows(DataValidationException.class,
                () -> commentValidator.validateUpdateComment(post, comment));

        comments.add(comment);
        post.setComments(comments);
        assertDoesNotThrow(() -> commentValidator.validateUpdateComment(post, comment));
    }

    @Test
    void testDeleteCommentValidator() {
        wrongId=3L;
        assertDoesNotThrow(() -> commentValidator.validateDeleteComment(rightId, rightId));
        assertThrows(DataValidationException.class,
                () -> commentValidator.validateDeleteComment(rightId, wrongId));
        assertThrows(NullPointerException.class,
                () -> commentValidator.validateDeleteComment(wrongId, wrongId));
    }
}