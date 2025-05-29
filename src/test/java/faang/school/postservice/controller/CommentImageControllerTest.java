package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.service.comment.CommentImageService;
import faang.school.postservice.validation.ValidateImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты контроллера изображений комментариев")
public class CommentImageControllerTest {

    @Mock
    private CommentImageService commentImageService;

    @Mock
    private ValidateImage validateImage;

    @InjectMocks
    private CommentImageController commentImageController;

    private final Long postId = 1L;
    private final Long commentId = 1L;
    private MockMultipartFile imageFile;
    private CommentViewDto commentViewDto;

    @BeforeEach
    void setUp() {
        imageFile = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image".getBytes()
        );

        commentViewDto = new CommentViewDto();
        commentViewDto.setId(commentId);
        commentViewDto.setPostId(postId);
    }

    @Nested
    @DisplayName("Загрузка изображения")
    class UploadImage {

        @Test
        @DisplayName("Успешная загрузка изображения")
        void givenValidImageFile_WhenUploadImage_ThenReturnOkResponse() {
            when(commentImageService.uploadImage(postId, commentId, imageFile))
                    .thenReturn(commentViewDto);

            ResponseEntity<CommentViewDto> response = commentImageController
                    .uploadImage(postId, commentId, imageFile);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(commentViewDto, response.getBody());
            verify(commentImageService).uploadImage(postId, commentId, imageFile);
        }

        @Test
        @DisplayName("Ошибка при пустом файле (с моком сервиса)")
        void givenEmptyFile_WhenUploadImage_ThenThrowDataValidationException() {
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file", "empty.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]
            );

            when(commentImageService.uploadImage(postId, commentId, emptyFile))
                    .thenThrow(new DataValidationException("Uploaded file is empty"));

            assertThrows(DataValidationException.class,
                    () -> commentImageController.uploadImage(postId, commentId, emptyFile));
        }

        @Test
        @DisplayName("Ошибка при невалидном типе файла")
        void givenInvalidFileType_WhenUploadImage_ThenThrowDataValidationException() {
            MockMultipartFile textFile = new MockMultipartFile(
                    "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "text".getBytes()
            );

            when(commentImageService.uploadImage(postId, commentId, textFile))
                    .thenThrow(new DataValidationException("Only image files are allowed"));

            assertThrows(DataValidationException.class,
                    () -> commentImageController.uploadImage(postId, commentId, textFile));
        }
    }

    @Nested
    @DisplayName("Удаление изображения")
    class DeleteImage {

        @Test
        @DisplayName("Успешное удаление изображения")
        void givenValidComment_WhenDeleteImage_ThenReturnOkResponse() {
            when(commentImageService.deleteImage(postId, commentId))
                    .thenReturn(commentViewDto);

            ResponseEntity<CommentViewDto> response = commentImageController
                    .deleteImage(postId, commentId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(commentViewDto, response.getBody());
            verify(commentImageService).deleteImage(postId, commentId);
        }

        @Test
        @DisplayName("Ошибка при несуществующем комментарии")
        void givenNonExistentComment_WhenDeleteImage_ThenThrowEntityNotFoundException() {
            when(commentImageService.deleteImage(postId, commentId))
                    .thenThrow(new EntityNotFoundException("Comment not found"));

            assertThrows(EntityNotFoundException.class,
                    () -> commentImageController.deleteImage(postId, commentId));
        }
    }
}