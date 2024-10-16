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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentServiceHandler commentServiceHandler;
    @InjectMocks
    private CommentService commentService;

    private Post post;
    private UserDto userDto;
    private Comment comment;
    private Comment comment2;


    @BeforeEach
    void setUp() {
        TestDataComment testDataComment = new TestDataComment();

        post = testDataComment.getPost();
        comment = testDataComment.getComment1();
        comment2 = testDataComment.getComment2();
        userDto = testDataComment.getUserDto();
    }

    @Nested
    class PositiveTests {
        @Test
        void testCreateCommentSuccess() {
            when(userServiceClient.getUser(userDto.getId())).thenReturn(userDto);
            when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
            when(commentRepository.save(comment)).thenReturn(comment);

            Comment createdComment = commentService.createComment(comment);
            assertNotNull(createdComment);
            assertEquals(userDto.getId(), createdComment.getAuthorId());

            verify(postRepository, atLeastOnce()).findById(post.getId());
            verify(userServiceClient, atLeastOnce()).getUser(userDto.getId());
            verify(commentServiceHandler, atLeastOnce()).userExistValidation(userDto.getId());
            verify(commentRepository, atLeastOnce()).save(comment);
        }

        @Test
        void testUpdateCommentSuccess() {
            when(userServiceClient.getUser(userDto.getId())).thenReturn(userDto);
            when(commentRepository.findById(userDto.getId())).thenReturn(Optional.ofNullable(comment));
            when(commentRepository.save(comment)).thenReturn(comment);

            Comment updatedComment = commentService.updateComment(comment);
            assertNotNull(updatedComment);
            assertEquals(comment.getContent(), updatedComment.getContent());

            verify(userServiceClient, atLeastOnce()).getUser(userDto.getId());
            verify(commentServiceHandler, atLeastOnce()).userExistValidation(userDto.getId());
            verify(commentServiceHandler, atLeastOnce()).editCommentByAuthorValidation(userDto, comment);
            verify(commentRepository, atLeastOnce()).save(comment);
        }

        @Test
        void testDeleteCommentSuccess() {
            long commentId = 1L;
            commentService.deleteComment(commentId);

            verify(commentServiceHandler, atLeastOnce()).commentExistsValidation(commentId);
            verify(commentRepository, atLeastOnce()).deleteById(commentId);
        }

        @Test
        void testFindAllCommentsSuccess() {
            List<Comment> comments = List.of(comment, comment2);

            when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(post.getId())).thenReturn(comments);

            List<Comment> foundComments = commentService.findAllComments(post.getId());
            assertNotNull(foundComments);
            assertEquals(2, foundComments.size());

            verify(commentServiceHandler, atLeastOnce()).postExistsValidation(post.getId());
            verify(commentRepository, atLeastOnce()).findAllByPostIdOrderByCreatedAtDesc(post.getId());
        }

        @Test
        void testGetAuthorIdsToBeBanned() {
            List<Long> ids = List.of(1L, 2L);
            when(commentService.getAuthorIdsToBeBanned()).thenReturn(ids);

            List<Long> actualIds = commentService.getAuthorIdsToBeBanned();

            verify(commentRepository, times(1)).findAuthorIdsToBeBanned();
            assertNotNull(actualIds);
            assertIterableEquals(ids, actualIds);

        }
    }

    @Nested
    class NegativeTests {
        @Test
        void testCreateComment_postNotFound_throwDataValidationException() {
            when(userServiceClient.getUser(userDto.getId())).thenReturn(userDto);
            when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> commentService.createComment(comment)
            );

            assertEquals("Post with ID: " + post.getId() + " not found.", exception.getMessage());
        }

        @Test
        void testUpdateComment_commentNotFound_throwDataValidationException() {
            when(userServiceClient.getUser(userDto.getId())).thenReturn(userDto);
            when(commentRepository.findById(userDto.getId())).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> commentService.updateComment(comment)
            );

            assertEquals("Comment with ID: " + comment.getId() + " not found.", exception.getMessage());
        }
    }
}
