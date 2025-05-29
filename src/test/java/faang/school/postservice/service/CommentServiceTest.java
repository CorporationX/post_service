package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.moderation.ModerationConfig;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.util.CommentModerationAsyncHandler;
import faang.school.postservice.validation.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private ModerationConfig moderationConfig;
    @Mock
    private CommentModerationAsyncHandler commentModerationAsyncHandler;

    @InjectMocks
    private CommentService commentService;

    private final Long postId = 1L;
    private final Long commentId = 1L;
    private final Long authorId = 1L;
    private CommentCreateDto commentCreateDto;
    private CommentViewDto commentViewDto;
    private Comment comment;
    private Post post;
    private List<Comment> comments;
    private final int batchesSize = 10;

    @BeforeEach
    void setUp() {
        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setContent("Test content");
        commentCreateDto.setAuthorId(authorId);
        commentCreateDto.setPostId(postId);

        commentViewDto = new CommentViewDto();
        commentViewDto.setId(commentId);
        commentViewDto.setContent("Test content");
        commentViewDto.setAuthorId(authorId);
        commentViewDto.setPostId(postId);

        post = new Post();
        post.setId(postId);

        comment = new Comment();
        comment.setId(commentId);
        comment.setContent("Test content");
        comment.setAuthorId(authorId);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());

        comment.setContent("Valid content");
        comments = List.of(comment);
    }

    @Nested
    @DisplayName("Создание комментария")
    class CreateComment {

        @Test
        @DisplayName("Успешное создание комментария")
        void givenValidCommentDataAndExistingPost_WhenCreateComment_ThenReturnCommentDto() {
            when(postRepository.findById(postId)).thenReturn(Optional.of(post));
            when(commentMapper.toEntity(commentCreateDto)).thenReturn(comment);
            when(commentRepository.save(comment)).thenReturn(comment);
            when(commentMapper.toViewDto(comment)).thenReturn(commentViewDto);

            CommentViewDto result = commentService.createComment(postId, commentCreateDto);

            assertNotNull(result);
            assertEquals(commentViewDto, result);
            verify(commentValidator).validateUserById(authorId);
            verify(commentRepository).save(comment);
        }

        @Test
        @DisplayName("Ошибка при несуществующем посте")
        void givenNonExistentPost_WhenCreateComment_ThenThrowEntityNotFoundException() {
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> commentService.createComment(postId, commentCreateDto));
        }

        @Test
        @DisplayName("Ошибка при несуществующем авторе")
        void givenNonExistentAuthor_WhenCreateComment_ThenThrowEntityNotFoundException() {
            doNothing().when(commentValidator).validatePostExists(postId);
            doThrow(new EntityNotFoundException("User with ID " + authorId + " not found"))
                    .when(commentValidator).validateUserById(authorId);

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> commentService.createComment(postId, commentCreateDto));

            assertEquals("User with ID " + authorId + " not found", exception.getMessage());
            verify(commentValidator).validatePostExists(postId);
            verify(commentValidator).validateUserById(authorId);
            verifyNoInteractions(postRepository);
        }
    }

    @Nested
    @DisplayName("Обновление комментария")
    class UpdateComment {

        @Test
        @DisplayName("Успешное обновление комментария")
        void givenValidCommentData_WhenUpdateComment_ThenReturnUpdatedCommentDto() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(commentRepository.save(comment)).thenReturn(comment);
            when(commentMapper.toViewDto(comment)).thenReturn(commentViewDto);

            CommentViewDto result = commentService.updateComment(postId, commentId, commentCreateDto);

            assertNotNull(result);
            assertEquals(commentViewDto, result);
            assertNotNull(comment.getUpdatedAt());
            verify(commentValidator).validateCommentBelongsToPost(comment, postId);
        }

        @Test
        @DisplayName("Ошибка при несуществующем комментарии")
        void givenNonExistentComment_WhenUpdateComment_ThenThrowEntityNotFoundException() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> commentService.updateComment(postId, commentId, commentCreateDto));
        }

        @Test
        @DisplayName("Ошибка при несоответствии postId")
        void givenMismatchedPostId_WhenUpdateComment_ThenThrowDataValidationException() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            doThrow(new DataValidationException("Invalid post ID"))
                    .when(commentValidator).validateCommentBelongsToPost(comment, postId);

            assertThrows(DataValidationException.class,
                    () -> commentService.updateComment(postId, commentId, commentCreateDto));
        }
    }

    @Nested
    @DisplayName("Получение комментариев для указанного поста")
    class GetComments {

        @Test
        @DisplayName("Успешное получение списка комментариев")
        void givenPostWithComments_WhenGetComments_ThenReturnCommentDtoList() {
            when(commentRepository.findAllByPostId(postId)).thenReturn(List.of(comment));
            when(commentMapper.toViewDto(comment)).thenReturn(commentViewDto);

            List<CommentViewDto> result = commentService.getCommentsByPostId(postId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(commentViewDto, result.get(0));
        }

        @Test
        @DisplayName("Получение пустого списка комментариев")
        void givenPostWithoutComments_WhenGetComments_ThenReturnEmptyList() {
            when(commentRepository.findAllByPostId(postId)).thenReturn(Collections.emptyList());

            List<CommentViewDto> result = commentService.getCommentsByPostId(postId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Удаление комментария")
    class DeleteComment {

        @Test
        @DisplayName("Успешное удаление комментария")
        void givenValidPostAndCommentIds_WhenDeleteComment_ThenSuccess() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

            assertDoesNotThrow(() -> commentService.deleteComment(postId, commentId));
            verify(commentRepository).delete(comment);
            verify(commentValidator).validateCommentBelongsToPost(comment, postId);
        }

        @Test
        @DisplayName("Ошибка при несуществующем комментарии")
        void givenNonExistentComment_WhenDeleteComment_ThenThrowEntityNotFoundException() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> commentService.deleteComment(postId, commentId));
        }

        @Test
        @DisplayName("Ошибка при несоответствии postId")
        void givenMismatchedPostId_WhenDeleteComment_ThenThrowDataValidationException() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            doThrow(new DataValidationException("Invalid post ID"))
                    .when(commentValidator).validateCommentBelongsToPost(comment, postId);

            assertThrows(DataValidationException.class,
                    () -> commentService.deleteComment(postId, commentId));
        }
    }

    @Nested
    @DisplayName("Получение комментария по ID")
    class GetCommentById {

        @Test
        @DisplayName("Успешное получение комментария по ID")
        void givenExistingCommentId_WhenGetCommentById_ThenReturnComment() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

            Comment result = commentService.getCommentById(commentId);

            assertNotNull(result);
            assertEquals(comment, result);
            verify(commentRepository).findById(commentId);
        }

        @Test
        @DisplayName("Ошибка при получении несуществующего комментария")
        void givenNonExistentCommentId_WhenGetCommentById_ThenThrowEntityNotFoundException() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> commentService.getCommentById(commentId));

            assertEquals("Comment with ID " + commentId + " not found", exception.getMessage());
            verify(commentRepository).findById(commentId);
        }

        @Test
        @DisplayName("Ошибка при передаче null в качестве ID")
        void givenNullCommentId_WhenGetCommentById_ThenThrowIllegalArgumentException() {
            assertThrows(EntityNotFoundException.class,
                    () -> commentService.getCommentById(null));
        }
    }

    @Nested
    @DisplayName("Модерация неверифицированных комментариев")
    class ModerateUnverifiedComments {
        @Test
        @DisplayName("Комментарий без нарушений проходит верификацию")
        void givenCleanComment_whenModerate_thenCommentVerified() {
            when(commentRepository.findAllByVerifiedAtIsNull()).thenReturn(comments);
            when(moderationConfig.getBatchSize()).thenReturn(batchesSize);
            when(commentModerationAsyncHandler.checkForProfanity(anyList()))
                    .thenReturn(CompletableFuture.completedFuture(null));

            assertDoesNotThrow(() -> commentService.moderateUnverifiedComment());

            verify(commentRepository).findAllByVerifiedAtIsNull();
        }

        @Test
        @DisplayName("Комментарий с запрещенными словами не проходит верификацию")
        void givenProfanityComment_whenModerate_thenCommentNotVerified() {
            comment.setContent("profanity bad word");

            when(commentRepository.findAllByVerifiedAtIsNull()).thenReturn(comments);
            when(moderationConfig.getBatchSize()).thenReturn(batchesSize);
            when(commentModerationAsyncHandler.checkForProfanity(anyList()))
                    .thenReturn(CompletableFuture.completedFuture(null));

            assertDoesNotThrow(() -> commentService.moderateUnverifiedComment());
        }

        @Test
        @DisplayName("Пустой список комментариев не требует модерации")
        void givenEmptyList_whenModerate_thenSkipProcessing() {
            List<Comment> emptyComments = Collections.emptyList();
            when(commentRepository.findAllByVerifiedAtIsNull()).thenReturn(emptyComments);

            assertDoesNotThrow(() -> commentService.moderateUnverifiedComment());

            verify(commentRepository).findAllByVerifiedAtIsNull();
            verify(commentModerationAsyncHandler, never()).checkForProfanity(anyList());
        }

        @Test
        @DisplayName("Большое количество комментариев разбивается на батчи")
        void givenLargeCommentList_whenModerate_thenProcessInBatches() {
            List<Comment> largeCommentList = Collections.nCopies(25, new Comment());
            when(commentRepository.findAllByVerifiedAtIsNull()).thenReturn(largeCommentList);
            when(moderationConfig.getBatchSize()).thenReturn(10);
            when(commentModerationAsyncHandler.checkForProfanity(anyList()))
                    .thenReturn(CompletableFuture.completedFuture(null));

            assertDoesNotThrow(() -> commentService.moderateUnverifiedComment());

            verify(commentModerationAsyncHandler, times(3)).checkForProfanity(anyList());
        }
    }
}