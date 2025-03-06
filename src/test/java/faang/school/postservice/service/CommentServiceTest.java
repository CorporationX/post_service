package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentResponse;
import faang.school.postservice.dto.comment.CommentUpdateRequest;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.exceptions.FileIsEmptyException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import faang.school.postservice.utils.ImageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private ValidateService validateService;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private PostService postService;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private CommentService commentService;

    @Mock
    private ModerationDictionary moderationDictionary;


    @Test
    void create_Success() {
        CreateCommentRequest request = new CreateCommentRequest(1L, "Текст", 2L);
        Post post = new Post();
        post.setId(2L);

        Comment commentSaved = Comment.builder()
                .authorId(1L)
                .id(1L)
                .content("Текст")
                .post(post)
                .build();

        doNothing().when(validateService).validateUser(request.userId());
        doNothing().when(validateService).validatePost(request.postId());
        doNothing().when(kafkaService)
                .sendCommentCreateMessage(any());

        when(commentRepository.save(any(Comment.class))).thenReturn(commentSaved);
        when(postService.getPostById(request.postId())).thenReturn(post);

        CommentResponse actualResponse = commentService.create(request);

        assertNotNull(actualResponse);
        assertEquals("Текст", actualResponse.content());

        verify(validateService).validateUser(1L);
        verify(validateService).validatePost(2L);
        verify(commentMapper).toEntity(request);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toCommentResponse(commentSaved);
    }

    @Test
    void update_ShouldThrowException_WhenCommentNotFound() {
        CommentUpdateRequest request = new CommentUpdateRequest(999L, "Текст");

        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.update(request));
        verify(commentRepository).findById(999L);
    }

    @Test
    void getAllByPostId_ShouldReturnSortedComments_WhenPostIdIsValid() {
        long postId = 2L;
        Comment c1 = new Comment();
        c1.setId(1L);
        c1.setCreatedAt(LocalDateTime.now().minusHours(2));

        Comment c2 = new Comment();
        c2.setId(2L);
        c2.setCreatedAt(LocalDateTime.now().minusHours(1));

        List<Comment> comments = List.of(c1, c2);

        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);

        // when
        List<CommentResponse> actualList = commentService.getAllByPostId(postId);

        // then
        assertEquals(2, actualList.size());
        // Проверяем, что сортировка по убыванию даты:
        assertEquals(2L, actualList.get(0).id());
        assertEquals(1L, actualList.get(1).id());

        verify(commentRepository).findAllByPostId(postId);
        verify(commentMapper, times(2)).toCommentResponse(any(Comment.class));
    }

    @Test
    void delete_ShouldDeleteComment_WhenCommentExists() {
        Long commentId = 2L;
        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentService.delete(commentId);

        verify(commentRepository).existsById(commentId);
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void delete_ShouldThrowException_WhenCommentDoesNotExist() {
        Long commentId = 2L;
        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> commentService.delete(commentId));
        verify(commentRepository).existsById(commentId);
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Успешная загрузка изображений для комментария")
    void uploadImage_Success() {
        // Данные теста
        Long commentId = 1L;
        String smallImageId = "small-img-123";
        String largeImageId = "large-img-456";
        MultipartFile mockFile = mock(MultipartFile.class);

        // Мокаем возвращаемое значение
        when(mockFile.isEmpty()).thenReturn(false);
        when(imageService.saveImage(any(MultipartFile.class), anyInt(), anyString()))
                .thenReturn(smallImageId)
                .thenReturn(largeImageId);
        // Мокаем комментарий
        Comment mockComment = new Comment();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));

        // Вызываем тестируемый метод
        commentService.uploadImage(commentId, mockFile);

        // Проверяем, что `setSmallImageFileKey` и `setLargeImageFileKey` вызвались корректно
        assertEquals(smallImageId, mockComment.getSmallImageFileKey());
        assertEquals(largeImageId, mockComment.getLargeImageFileKey());

        // Проверяем, что `saveImage` вызвался дважды
        verify(imageService, times(2)).saveImage(any(MultipartFile.class), anyInt(), anyString());
    }

    @Test
    @DisplayName("Ошибка при загрузке пустого файла")
    void uploadImage_ThrowsFileIsEmptyException() {
        // Данные теста
        Long commentId = 1L;
        Comment comment = new Comment();

        MultipartFile mockFile = mock(MultipartFile.class);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(mockFile.isEmpty()).thenReturn(true);

        // Проверяем, что выбрасывается исключение
        assertThrows(FileIsEmptyException.class, () -> commentService.uploadImage(commentId, mockFile));

        // Проверяем, что `saveImage` НЕ вызывался
        verify(imageService, never()).saveImage(any(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Ошибка при сохранении изображения в ImageService")
    void uploadImage_SaveImageFailure() {
        // Данные теста
        Long commentId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);

        // Мокаем исключение в `saveImage`
        when(imageService.saveImage(any(MultipartFile.class), anyInt(), anyString()))
                .thenThrow(new RuntimeException("Ошибка при сохранении изображения"));

        Comment mockComment = new Comment();
        when(commentRepository.findById(commentId)).thenReturn(java.util.Optional.of(mockComment));

        // Проверяем, что метод выбрасывает RuntimeException
        assertThrows(RuntimeException.class, () -> commentService.uploadImage(commentId, mockFile));

        // Проверяем, что `setSmallImageFileKey` не вызывался
        assertNull(mockComment.getSmallImageFileKey());
        assertNull(mockComment.getLargeImageFileKey());

        // Проверяем, что `saveImage` вызвался только один раз (и упал)
        verify(imageService, times(1)).saveImage(eq(mockFile), eq(170), anyString());
        verify(imageService, never()).saveImage(any(MultipartFile.class), eq(1080), anyString());
    }

    @Test
    void shouldModerateCommentsCorrectly() {
        List<Comment> comments = List.of(
                Comment.builder().id(1L).content("Good comment").verified(false).verifiedAt(null).build(),
                Comment.builder().id(2L).content("Bad word here").verified(false).verifiedAt(null).build()
        );

        when(moderationDictionary.containsBadWord("Good comment")).thenReturn(false);
        when(moderationDictionary.containsBadWord("Bad word here")).thenReturn(true);

        CompletableFuture<Void> future = commentService.moderateComments(comments);
        future.join();

        assertTrue(comments.get(0).getVerified());
        assertFalse(comments.get(1).getVerified());

        assertNotNull(comments.get(0).getVerifiedAt());
        assertNotNull(comments.get(1).getVerifiedAt());

        verify(commentRepository).saveAll(comments);
    }

}