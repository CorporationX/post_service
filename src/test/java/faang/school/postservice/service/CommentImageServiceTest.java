package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ImageProcessingException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.comment.CommentImageService;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.s3.S3StorageService;
import faang.school.postservice.service.util.ImageProcessor;
import faang.school.postservice.service.util.ProcessedImages;
import faang.school.postservice.validation.CommentValidator;
import faang.school.postservice.validation.ValidateImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса изображений комментариев")
public class CommentImageServiceTest {

    @Mock
    private CommentService commentService;
    @Mock
    private S3StorageService storageService;
    @Mock
    private ImageProcessor imageProcessor;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private ValidateImage validateImage;

    @InjectMocks
    private CommentImageService commentImageService;

    private final Long postId = 1L;
    private final Long commentId = 1L;
    private MockMultipartFile imageFile;
    private Comment comment;
    private CommentViewDto commentViewDto;
    private BufferedImage testImage;
    private ProcessedImages processedImages;

    @BeforeEach
    void setUp() {
        imageFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[1024]);

        testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        processedImages = new ProcessedImages(testImage, testImage);

        Post post = new Post();
        post.setId(postId);

        comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post);

        commentViewDto = new CommentViewDto();
        commentViewDto.setId(commentId);
        commentViewDto.setPostId(postId);
    }

    @Nested
    @DisplayName("Загрузка изображения")
    class UploadImage {

        @Test
        @DisplayName("Успешная загрузка и обработка изображения")
        void givenValidImage_WhenUploadImage_ThenProcessAndSaveImages() throws IOException {
            when(commentService.getCommentById(commentId)).thenReturn(comment);
            when(imageProcessor.convertToBufferedImage(imageFile)).thenReturn(testImage);
            when(imageProcessor.processImage(testImage)).thenReturn(processedImages);
            when(commentService.updateCommentEntity(comment)).thenReturn(commentViewDto);

            CommentViewDto result = commentImageService.uploadImage(postId, commentId, imageFile);

            assertNotNull(result);
            verify(validateImage).validateImageFile(imageFile);
            verify(commentValidator).validateCommentBelongsToPost(comment, postId);
            verify(storageService, times(2)).uploadToS3(anyString(), any(), eq("image/jpeg"));
            assertNotNull(comment.getLargeImageFileKey());
            assertNotNull(comment.getSmallImageFileKey());
        }

        @Test
        @DisplayName("Ошибка при невалидном изображении")
        void givenInvalidImage_WhenUploadImage_ThenThrowException() {
            doThrow(new DataValidationException("Invalid image"))
                    .when(validateImage).validateImageFile(imageFile);

            assertThrows(DataValidationException.class,
                    () -> commentImageService.uploadImage(postId, commentId, imageFile));
        }

        @Test
        @DisplayName("Ошибка при конвертации изображения")
        void givenCorruptedImage_WhenUploadImage_ThenThrowException() throws IOException {
            when(commentService.getCommentById(commentId)).thenReturn(comment);
            when(imageProcessor.convertToBufferedImage(imageFile))
                    .thenThrow(new IOException("Conversion error"));

            assertThrows(ImageProcessingException.class,
                    () -> commentImageService.uploadImage(postId, commentId, imageFile));
        }

        @Test
        @DisplayName("Ошибка при загрузке в хранилище")
        void givenStorageError_WhenUploadImage_ThenThrowException() throws IOException {
            when(commentService.getCommentById(commentId)).thenReturn(comment);
            when(imageProcessor.convertToBufferedImage(imageFile)).thenReturn(testImage);
            when(imageProcessor.processImage(testImage)).thenReturn(processedImages);
            doThrow(new IOException("Storage error"))
                    .when(storageService).uploadToS3(anyString(), any(), anyString());

            assertThrows(ImageProcessingException.class,
                    () -> commentImageService.uploadImage(postId, commentId, imageFile));
        }
    }

    @Nested
    @DisplayName("Удаление изображения")
    class DeleteImage {

        @Test
        @DisplayName("Успешное удаление изображения")
        void givenCommentWithImages_WhenDeleteImage_ThenRemoveImages() {
            comment.setLargeImageFileKey("large-key");
            comment.setSmallImageFileKey("small-key");
            when(commentService.getCommentById(commentId)).thenReturn(comment);
            when(commentService.updateCommentEntity(comment)).thenReturn(commentViewDto);

            CommentViewDto result = commentImageService.deleteImage(postId, commentId);

            assertNotNull(result);
            verify(storageService).deleteFile("large-key");
            verify(storageService).deleteFile("small-key");
            assertNull(comment.getLargeImageFileKey());
            assertNull(comment.getSmallImageFileKey());
        }

        @Test
        @DisplayName("Удаление комментария без изображений")
        void givenCommentWithoutImages_WhenDeleteImage_ThenSuccess() {
            when(commentService.getCommentById(commentId)).thenReturn(comment);
            when(commentService.updateCommentEntity(comment)).thenReturn(commentViewDto);

            assertDoesNotThrow(() -> commentImageService.deleteImage(postId, commentId));

            verify(storageService, never()).deleteFile(any());

            assertNull(comment.getLargeImageFileKey());
            assertNull(comment.getSmallImageFileKey());
        }

        @Test
        @DisplayName("Ошибка при несуществующем комментарии")
        void givenNonExistentComment_WhenDeleteImage_ThenThrowException() {
            when(commentService.getCommentById(commentId))
                    .thenThrow(new EntityNotFoundException("Comment not found"));

            assertThrows(EntityNotFoundException.class,
                    () -> commentImageService.deleteImage(postId, commentId));
        }

        @Test
        @DisplayName("Ошибка при удалении из хранилища")
        void givenStorageError_WhenDeleteImage_ThenStillClearReferences() {
            comment.setLargeImageFileKey("large-key");
            comment.setSmallImageFileKey("small-key");
            when(commentService.getCommentById(commentId)).thenReturn(comment);
            when(commentService.updateCommentEntity(comment)).thenReturn(commentViewDto);

            doThrow(new ImageProcessingException("Storage error"))
                    .when(storageService).deleteFile("large-key");

            assertDoesNotThrow(() -> commentImageService.deleteImage(postId, commentId));
            assertNull(comment.getLargeImageFileKey());
            assertNull(comment.getSmallImageFileKey());
        }
    }
}