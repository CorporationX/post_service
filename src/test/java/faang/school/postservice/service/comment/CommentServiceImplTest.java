package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static faang.school.postservice.service.comment.CommentServiceImplTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки функциональности сервиса комментариев.
 * <p>
 * Содержит unit-тесты для методов создания, обновления, удаления и получения комментариев.
 * Проверяет корректность работы сервиса в различных сценариях, включая обработку ошибок.
 * </p>
 *
 * @author bozya
 * @since 21.08.2025
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Post post;
    private Comment comment;
    private CommentCreateDto createDto;
    private CommentUpdateDto updateDto;
    private CommentViewDto viewDto;

    @BeforeEach
    void setUp() {
        post = Post.builder().id(POST_ID).build();
        comment = Comment.builder()
                .id(COMMENT_ID)
                .content(CONTENT)
                .authorId(USER_ID)
                .post(post)
                .largeImageFileKey(LARGE_IMAGE_KEY)
                .smallImageFileKey(SMALL_IMAGE_KEY)
                .build();

        createDto = new CommentCreateDto(CONTENT, USER_ID, POST_ID, LARGE_IMAGE_KEY, SMALL_IMAGE_KEY);
        updateDto = new CommentUpdateDto("Updated content", USER_ID, POST_ID, "updated-large", "updated-small");
        viewDto = new CommentViewDto(COMMENT_ID, CONTENT, USER_ID, POST_ID, LARGE_IMAGE_KEY, SMALL_IMAGE_KEY);
    }


    /**
     * Тестирует успешное создание комментария, когда пост существует.
     * Проверяет, что возвращается корректный CommentViewDto и вызываются ожидаемые методы репозиториев.
     */
    @Test
    void createPostExistsReturnsCommentViewDto() {
        when(postRepository.findPostOrThrow(POST_ID)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toViewDto(comment)).thenReturn(viewDto);

        CommentViewDto result = commentService.create(createDto, POST_ID);

        assertNotNull(result);
        assertEquals(COMMENT_ID, result.id());
        assertEquals(CONTENT, result.content());
        assertEquals(USER_ID, result.authorId());
        assertEquals(POST_ID, result.postId());

        verify(postRepository).findPostOrThrow(POST_ID);
        verify(userContext).getUserId();
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toViewDto(comment);
    }

    /**
     * Тестирует создание комментария, когда пост не найден.
     * Проверяет, что выбрасывается исключение EntityNotFoundException с корректным сообщением.
     */
    @Test
    void createPostNotFoundThrowsEntityNotFoundException() {
        when(postRepository.findPostOrThrow(POST_ID))
                .thenThrow(new EntityNotFoundException("Пост с id " + POST_ID + " не найден"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.create(createDto, POST_ID));

        assertEquals("Пост с id " + POST_ID + " не найден", exception.getMessage());
        verify(postRepository).findPostOrThrow(POST_ID);
        verifyNoInteractions(commentRepository, commentMapper);
    }

    /**
     * Тестирует успешное удаление комментария, когда комментарий существует.
     * Проверяет, что метод завершается без исключений и вызываются ожидаемые методы репозитория.
     */
    @Test
    void deleteCommentExistsDeletesComment() {
        when(commentRepository.findByIdAndPostIdOrThrow(POST_ID, COMMENT_ID)).thenReturn(comment);
        doNothing().when(commentRepository).delete(comment);

        assertDoesNotThrow(() -> commentService.delete(POST_ID, COMMENT_ID));

        verify(commentRepository).findByIdAndPostIdOrThrow(POST_ID, COMMENT_ID);
        verify(commentRepository).delete(comment);
    }

    /**
     * Тестирует удаление комментария, когда комментарий не найден.
     * Проверяет, что выбрасывается исключение EntityNotFoundException с корректным сообщением.
     */
    @Test
    void deleteCommentNotFoundThrowsEntityNotFoundException() {
        when(commentRepository.findByIdAndPostIdOrThrow(POST_ID, COMMENT_ID))
                .thenThrow(new EntityNotFoundException("Комментарий с id " + COMMENT_ID + " не найден"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.delete(POST_ID, COMMENT_ID));

        assertEquals("Комментарий с id " + COMMENT_ID + " не найден", exception.getMessage());
        verify(commentRepository).findByIdAndPostIdOrThrow(POST_ID, COMMENT_ID);
        verify(commentRepository, never()).delete(any());
    }

    /**
     * Тестирует успешное обновление комментария, когда комментарий существует и пользователь является автором.
     * Проверяет, что возвращается обновленный CommentViewDto и вызываются ожидаемые методы репозиториев.
     */
    @Test
    void updateCommentExistsAndUserIsAuthorReturnsUpdatedComment() {
        Comment updatedComment = Comment.builder()
                .id(COMMENT_ID)
                .content("Updated content")
                .authorId(USER_ID)
                .post(post)
                .largeImageFileKey("updated-large")
                .smallImageFileKey("updated-small")
                .build();

        CommentViewDto updatedViewDto = new CommentViewDto(
                COMMENT_ID, "Updated content", USER_ID, POST_ID, "updated-large", "updated-small"
        );

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.update(
                COMMENT_ID, POST_ID, USER_ID,
                "Updated content", "updated-large", "updated-small"
        )).thenReturn(Optional.of(updatedComment));
        when(commentMapper.toViewDto(updatedComment)).thenReturn(updatedViewDto);

        CommentViewDto result = commentService.update(POST_ID, COMMENT_ID, updateDto);

        assertNotNull(result);
        assertEquals("Updated content", result.content());
        assertEquals("updated-large", result.largeImageFileKey());
        assertEquals("updated-small", result.smallImageFileKey());

        verify(userContext).getUserId();
        verify(commentRepository).update(
                COMMENT_ID, POST_ID, USER_ID,
                "Updated content", "updated-large", "updated-small"
        );
        verify(commentMapper).toViewDto(updatedComment);
    }

    /**
     * Тестирует обновление комментария, когда комментарий не найден или пользователь не является автором.
     * Проверяет, что выбрасывается исключение EntityNotFoundException с корректным сообщением.
     */
    @Test
    void updateCommentNotFoundThrowsEntityNotFoundException() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.update(
                COMMENT_ID, POST_ID, USER_ID,
                "Updated content", "updated-large", "updated-small"
        )).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.update(POST_ID, COMMENT_ID, updateDto));

        assertEquals("Комментарий не найден или у вас нет прав для его редактирования", exception.getMessage());

        verify(userContext).getUserId();
        verify(commentRepository).update(
                COMMENT_ID, POST_ID, USER_ID,
                "Updated content", "updated-large", "updated-small"
        );
        verify(commentMapper, never()).toViewDto(any());
    }

    /**
     * Тестирует получение всех комментариев поста, когда пост существует и содержит комментарии.
     * Проверяет, что возвращается непустой список CommentViewDto и вызываются ожидаемые методы репозиториев.
     */
    @Test
    void getAllCommentByPostIdPostExistsWithCommentsReturnsCommentList() {
        List<Comment> comments = List.of(comment);
        List<CommentViewDto> viewDtos = List.of(viewDto);

        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);
        when(commentMapper.toViewDto(comment)).thenReturn(viewDto);

        List<CommentViewDto> result = commentService.getAllCommentByPostId(POST_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(viewDto, result.get(0));

        verify(postRepository).existsById(POST_ID);
        verify(commentRepository).findAllByPostId(POST_ID);
        verify(commentMapper).toViewDto(comment);
    }

    /**
     * Тестирует получение всех комментариев поста, когда пост существует, но не содержит комментариев.
     * Проверяет, что возвращается пустой список и вызываются ожидаемые методы репозиториев.
     */
    @Test
    void getAllCommentByPostIdPostExistsNoCommentsReturnsEmptyList() {
        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(List.of());

        List<CommentViewDto> result = commentService.getAllCommentByPostId(POST_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository).existsById(POST_ID);
        verify(commentRepository).findAllByPostId(POST_ID);
        verify(commentMapper, never()).toViewDto(any());
    }

    /**
     * Тестирует получение всех комментариев поста, когда пост не найден.
     * Проверяет, что выбрасывается исключение EntityNotFoundException с корректным сообщением.
     */
    @Test
    void getAllCommentByPostIdPostNotFoundThrowsEntityNotFoundException() {
        when(postRepository.existsById(POST_ID)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentService.getAllCommentByPostId(POST_ID));

        assertEquals("Пост с id " + POST_ID + " не найден", exception.getMessage());

        verify(postRepository).existsById(POST_ID);
        verify(commentRepository, never()).findAllByPostId(anyLong());
        verify(commentMapper, never()).toViewDto(any());
    }

    /**
     * Тестирует поиск поста через метод создания комментария, когда пост существует.
     * Проверяет, что метод findById репозитория постов вызывается с правильным идентификатором.
     */
    @Test
    void findPostByIdThroughCreateMethodPostExists() {
        when(postRepository.findPostOrThrow(POST_ID)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toViewDto(comment)).thenReturn(viewDto);

        commentService.create(createDto, POST_ID);

        verify(postRepository).findPostOrThrow(POST_ID);
    }

    /**
     * Тестирует поиск поста через метод создания комментария, когда пост не найден.
     * Проверяет, что выбрасывается исключение EntityNotFoundException.
     */
    @Test
    void findPostByIdThroughCreateMethodPostNotFound() {
        when(postRepository.findPostOrThrow(POST_ID))
                .thenThrow(new EntityNotFoundException("Пост с id " + POST_ID + " не найден"));

        assertThrows(EntityNotFoundException.class, () -> commentService.create(createDto, POST_ID));

        verify(postRepository).findPostOrThrow(POST_ID);
    }

    /**
     * Тестирует поиск комментария через метод удаления, когда комментарий существует.
     * Проверяет, что метод findByIdAndPostId репозитория комментариев вызывается с правильными параметрами.
     */
    @Test
    void findCommentByIdThroughDeleteMethodCommentExists() {
        when(commentRepository.findByIdAndPostIdOrThrow(POST_ID, COMMENT_ID)).thenReturn(comment);
        doNothing().when(commentRepository).delete(comment);

        commentService.delete(POST_ID, COMMENT_ID);

        verify(commentRepository).findByIdAndPostIdOrThrow(POST_ID, COMMENT_ID);
    }

    /**
     * Тестирует поиск комментария через метод удаления, когда комментарий не найден.
     * Проверяет, что выбрасывается исключение EntityNotFoundException.
     */
    @Test
    void findCommentByIdThroughDeleteMethodCommentNotFound() {
        when(commentRepository.findByIdAndPostIdOrThrow(POST_ID, COMMENT_ID))
                .thenThrow(new EntityNotFoundException("Комментарий с id " + COMMENT_ID + " не найден"));

        assertThrows(EntityNotFoundException.class, () -> commentService.delete(POST_ID, COMMENT_ID));
        verify(commentRepository).findByIdAndPostIdOrThrow(POST_ID, COMMENT_ID);
    }
}