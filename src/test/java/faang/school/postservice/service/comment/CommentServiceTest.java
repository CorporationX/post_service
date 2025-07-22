package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.NotResourceOwnerException;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class CommentServiceTest {
    @InjectMocks
    CommentService commentService;
    @Mock
    private UserContext userContext;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private CommentMapperImpl mapper;
    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    private static final long POST_ID = 1;
    private static final long COMMENT_ID = 2;
    private static final long USER_ID = 3;
    private static final long OTHER_USER_ID = 4;
    private static final String CONTENT = "Test a content";
    private static final String NEW_CONTENT = "Test a new content";

    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentDto = prepareCommentDto(null, CONTENT);
    }

    @Test
    @DisplayName("Успешное создание комментария")
    void positive_shouldCreateComment() {
        CommentDto expected = prepareCommentDto(COMMENT_ID, CONTENT);
        preparePositiveCreateBehavior();

        CommentDto actual = commentService.create(commentDto);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Успешное обновлние комментария")
    void positive_shouldUpdateCommentById() {
        CommentDto commentDto = prepareCommentDto(null, NEW_CONTENT);
        CommentDto expected = prepareCommentDto(COMMENT_ID, NEW_CONTENT);
        preparePositiveUpdateBehavior();

        CommentDto actual = commentService.update(COMMENT_ID, commentDto);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Успешное удаление комментария")
    void positive_shouldDeleteCommentById() {
        preparePositiveDeleteBehavior();

        commentService.delete(COMMENT_ID);
        verify(commentRepository, times(1)).deleteById(COMMENT_ID);
    }

    @Test
    @DisplayName("Успешное получение комментариев по id поста")
    void positive_shouldFindAllCommentByPostId() {
        List<CommentDto> expected  = List.of(prepareCommentDto(COMMENT_ID, CONTENT));
        when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(POST_ID))
                .thenReturn(List.of(prepareExistsComment().get()));

        List<CommentDto> actual = commentService.findAllByPostId(POST_ID);

        verify(commentRepository, times(1)).findAllByPostIdOrderByCreatedAtDesc(POST_ID);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Ошибка создания комментария - юзер не автор")
    void negative_whenUserNotOwner_createThrowsException() {
        when(userContext.getUserId()).thenReturn(OTHER_USER_ID);

        verify(commentRepository, never()).save(any(Comment.class));
        assertThrows(NotResourceOwnerException.class,
                     () -> commentService.create(commentDto));
    }

    @Test
    @DisplayName("Ошибка создания комментария - юзер не найден")
    void negative_whenUserNotFound_createThrowsException() {
        String expectedMessage = "User " + USER_ID + " not found";
        when(userContext.getUserId()).thenReturn(USER_ID);

        verify(commentRepository, never()).save(any(Comment.class));
        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> commentService.create(commentDto)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка создания комментария - пост не найден")
    void negative_whenPostNotFound_createThrowsException() {
        String expectedMessage = "Post " + POST_ID + " not found";
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(prepareUser());
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        verify(commentRepository, never()).save(any(Comment.class));
        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> commentService.create(commentDto)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка обновления комментария - юзер не автор")
    void negative_whenUserNotOwner_updateThrowsException() {
        when(userContext.getUserId()).thenReturn(OTHER_USER_ID);

        verify(commentRepository, never()).save(any(Comment.class));
        assertThrows(NotResourceOwnerException.class,
                     () -> commentService.update(COMMENT_ID, commentDto));
    }

    @Test
    @DisplayName("Ошибка обновления комментария - юзер не найден")
    void negative_whenUserNotFound_updateThrowsException() {
        String expectedMessage = "User " + USER_ID + " not found";
        when(userContext.getUserId()).thenReturn(USER_ID);

        verify(commentRepository, never()).save(any(Comment.class));
        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> commentService.update(COMMENT_ID, commentDto)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка обновления комментария - комментарий не найден")
    void negative_whenCommentNotFound_updateThrowsException() {
        String expectedMessage = "Comment " + COMMENT_ID + " not found";
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(prepareUser());
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        verify(commentRepository, never()).save(any(Comment.class));
        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> commentService.update(COMMENT_ID, commentDto)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка удаления комментария - юзер не автор")
    void negative_whenUserNotOwner_deleteThrowsException() {
        when(userContext.getUserId()).thenReturn(OTHER_USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(prepareExistsComment());

        verify(commentRepository, never()).deleteById(anyLong());
        assertThrows(NotResourceOwnerException.class,
                     () -> commentService.delete(COMMENT_ID));
    }

    @Test
    @DisplayName("Ошибка удаления комментария - юзер не найден")
    void negative_whenUserNotFound_deleteThrowsException() {
        String expectedMessage = "User " + USER_ID + " not found";
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(prepareExistsComment());

        verify(commentRepository, never()).deleteById(anyLong());
        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> commentService.delete(COMMENT_ID)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка удаления комментария - комментарий не найден")
    void negative_whenCommentNotFound_deleteThrowsException() {
        String expectedMessage = "Comment " + COMMENT_ID + " not found";
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        verify(commentRepository, never()).deleteById(anyLong());
        String actualMessage = assertThrows(EntityNotFoundException.class,
                                            () -> commentService.delete(COMMENT_ID)).getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    // ----------------------

    private CommentDto prepareCommentDto(Long commentId, String content) {
        return new CommentDto(commentId, content, USER_ID, 0, POST_ID, null, null);
    }

    private UserDto prepareUser() {
        return new UserDto(USER_ID, null, null);
    }

    private Optional<Post> preparePost() {
        return Optional.of(Post.builder()
                                   .id(POST_ID)
                                   .build());
    }

    private Optional<Comment> prepareExistsComment() {
        return Optional.of(Comment.builder()
                                   .id(COMMENT_ID)
                                   .content(CONTENT)
                                   .authorId(USER_ID)
                                   .post(preparePost().get())
                                   .build());
    }

    private void preparePositiveCreateBehavior() {
        prepareCommonPositiveBehavior();
        when(postRepository.findById(POST_ID)).thenReturn(preparePost());
        when(commentRepository.save(commentCaptor.capture()))
                .thenAnswer(invocation -> {
                    Comment comment = commentCaptor.getValue();
                    comment.setId(COMMENT_ID);
                    return comment;
                });
    }

    private void preparePositiveUpdateBehavior() {
        prepareCommonPositiveBehavior();
        when(commentRepository.findById(COMMENT_ID)).thenReturn(prepareExistsComment());
        when(commentRepository.save(commentCaptor.capture()))
                .thenAnswer(invocation -> {
                    Comment comment = commentCaptor.getValue();
                    comment.setId(COMMENT_ID);
                    return comment;
                });
    }

    private void preparePositiveDeleteBehavior() {
        prepareCommonPositiveBehavior();
        when(commentRepository.findById(COMMENT_ID)).thenReturn(prepareExistsComment());
    }

    private void prepareCommonPositiveBehavior() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(prepareUser());
    }
}