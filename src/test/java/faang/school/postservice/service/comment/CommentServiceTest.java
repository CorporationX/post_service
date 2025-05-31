package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validation.comment.CommentValidation;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final long COMMENT_ID = 1L;
    private static final long POST_ID = 2L;
    private static final long USER_ID = 3L;
    private static final String CONTENT = "content";

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentValidation commentValidation;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private Comment commentNew;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        Post post = new Post();
        post.setId(POST_ID);
        commentNew = Comment.builder()
                .id(COMMENT_ID)
                .authorId(USER_ID)
                .post(post)
                .content(CONTENT)
                .build();

        comment = Comment.builder()
                .authorId(USER_ID)
                .post(post)
                .content(CONTENT)
                .build();
        userDto = new UserDto(USER_ID, "User", "user@mail.ru");
    }

    @Test
    void testCreateCommentWhenCommentCreated() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        doNothing().when(commentValidation).validateLengthContentComment(CONTENT);
        doNothing().when(commentValidation).checkAuthorEqualsUser(USER_ID, USER_ID);
        doNothing().when(commentValidation).validatePostExists(POST_ID);
        when(commentRepository.save(comment)).thenReturn(commentNew);

        Comment result = commentService.createComment(comment);

        assertEquals(COMMENT_ID, result.getId());
        verify(commentValidation).validateLengthContentComment(CONTENT);
        verify(commentValidation).checkAuthorEqualsUser(USER_ID, USER_ID);
        verify(commentValidation).validatePostExists(POST_ID);
        verify(userServiceClient).getUser(USER_ID);
        verify(commentRepository).save(comment);
    }

    @Test
    void testUpdateCommentWhenCommentUpdated() {
        String otherContent = CONTENT + "other";
        commentNew.setContent(otherContent);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentNew));
        doNothing().when(commentValidation).validateLengthContentComment(otherContent);
        doNothing().when(commentValidation).checkContentNotEquals(anyString(), anyString());
        when(commentRepository.save(commentNew)).thenReturn(commentNew);

        Comment result = commentService.updateComment(commentNew);

        assertEquals(commentNew.getId(), result.getId());
        assertEquals(commentNew.getContent(), result.getContent());
        verify(commentValidation).validateLengthContentComment(otherContent);
        verify(commentValidation).checkContentNotEquals(anyString(), anyString());
        verify(commentRepository).save(commentNew);
    }

    @Test
    void testGetAllCommentWhenPostExists() {
        List<Comment> comments = List.of(comment, commentNew);

        doNothing().when(commentValidation).validatePostExists(POST_ID);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<Comment> result = commentService.getAllComments(POST_ID);

        assertEquals(comments.get(0).getId(), result.get(0).getId());
        assertEquals(comments.get(1).getId(), result.get(1).getId());
        assertEquals(comments.size(), result.size());
        verify(commentValidation).validatePostExists(POST_ID);
        verify(commentRepository).findAllByPostId(POST_ID);
    }


    @Test
    void testGetAllCommentWhenListCommentsEmpty() {
        doNothing().when(commentValidation).validatePostExists(POST_ID);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(Collections.emptyList());

        List<Comment> result = commentService.getAllComments(POST_ID);

        assertEquals(Collections.emptyList(), result);
        verify(commentValidation).validatePostExists(POST_ID);
        verify(commentRepository).findAllByPostId(POST_ID);
    }

    @Test
    void testDeleteCommentWhenCommentExists() {
        doNothing().when(commentValidation).validateCommentExists(COMMENT_ID);
        doNothing().when(commentValidation).checkAuthorEqualsUser(USER_ID, USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentNew));
        doNothing().when(commentRepository).deleteById(COMMENT_ID);

        assertDoesNotThrow(() -> commentService.deleteComment(COMMENT_ID, USER_ID));
        verify(commentValidation).validateCommentExists(COMMENT_ID);
        verify(commentValidation).checkAuthorEqualsUser(USER_ID, USER_ID);
        verify(commentRepository).deleteById(COMMENT_ID);
    }

    @Test
    void testGetCommentWhenCommentNotExists() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.getComment(COMMENT_ID));
    }
}
