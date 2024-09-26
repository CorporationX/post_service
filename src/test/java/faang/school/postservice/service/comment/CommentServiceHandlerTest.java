package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.test_data.TestDataComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceHandlerTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private CommentServiceHandler commentServiceHandler;

    private UserDto userDto;
    private UserDto userDto2;
    private Comment comment;
    private Post post;

    @BeforeEach
    void setUp() {
        TestDataComment testDataComment = new TestDataComment();

        userDto = testDataComment.getUserDto();
        userDto2 = testDataComment.getUserDto2();
        comment = testDataComment.getComment1();
        post = testDataComment.getPost();
    }

    @Test
    void testUserExistsByIdValidation_UserNotFound_throwDataValidationException() {
        when(userServiceClient.getUser(userDto.getId())).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentServiceHandler.userExistsByIdValidation(userDto.getId())
        );

        assertEquals("Author with ID: " + userDto.getId() + " not found.", exception.getMessage());
    }

    @Test
    void testCommentExistsByIdValidation_CommentNotFound_throwDataValidationException() {
        when(commentRepository.existsById(comment.getId())).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentServiceHandler.commentExistsByIdValidation(comment.getId())
        );

        assertEquals("Comment with ID: " + comment.getId() + " not found.", exception.getMessage());
    }

    @Test
    void testPostExistsByIdValidation_PostNotFound_throwDataValidationException() {
        when(postRepository.existsById(post.getId())).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentServiceHandler.postExistsByIdValidation(post.getId())
        );

        assertEquals("Post with ID: " + post.getId() + " not found.", exception.getMessage());
    }

    @Test
    void testEditCommentByAuthorValidation_commentEditByAnotherUser_throwDataValidationException() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> commentServiceHandler.editCommentByAuthorValidation(userDto2, comment)
        );

        assertEquals("Only Author can edit comment", exception.getMessage());
    }
}