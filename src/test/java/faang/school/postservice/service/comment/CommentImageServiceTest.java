package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.exception.file.FileNotFoundException;
import faang.school.postservice.model.comment.CommentImage;
import faang.school.postservice.repository.comment.CommentImageRepository;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.service.image.ImageService;
import faang.school.postservice.validation.image.CommentImageFileValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentImageServiceTest {

    @Mock
    private ImageService imageService;

    @Mock
    private CommentImageFileValidator imageFileValidator;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentImageRepository commentImageRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private CommentImageService commentImageService;

    @Test
    void uploadImageForComment_shouldUploadAndSaveMetadata() {
        Long commentId = 1L;
        Long userId = 42L;
        MultipartFile file = mock(MultipartFile.class);
        ImageResponseDto dto = new ImageResponseDto("key1", "previewKey", "image/jpeg", 12345L);

        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(imageService.uploadToS3(file)).thenReturn(dto);
        when(userContext.getUserId()).thenReturn(userId);

        ImageResponseDto result = commentImageService.uploadImageForComment(commentId, file);

        assertEquals(dto, result);
        verify(imageFileValidator).validate(file);
        verify(commentImageRepository).save(argThat(img ->
                img.getCommentId().equals(commentId) &&
                        img.getUserId().equals(userId) &&
                        img.getFileKey().equals("key1")
        ));
    }

    @Test
    void uploadImageForComment_shouldThrow_whenCommentNotFound() {
        when(commentRepository.existsById(999L)).thenReturn(false);

        MultipartFile file = mock(MultipartFile.class);

        assertThrows(CommentNotFoundException.class, () ->
                commentImageService.uploadImageForComment(999L, file)
        );
    }

    @Test
    void downloadImageByCommentId_shouldReturnImage() {
        Long commentId = 1L, imageId = 100L;
        CommentImage img = buildImage(commentId);
        Resource mockResource = mock(Resource.class);

        when(commentImageRepository.findByIdAndCommentId(imageId, commentId))
                .thenReturn(Optional.of(img));
        when(imageService.download(img.getFileKey())).thenReturn(mockResource);

        ImageDownloadDto result = commentImageService.downloadImageByCommentId(commentId, imageId);

        assertEquals(img.getFileKey(), result.getOriginalFileName());
        assertEquals(img.getContentType(), result.getContentType());
        assertEquals(mockResource, result.getResource());
    }

    @Test
    void downloadPreviewByCommentId_shouldReturnPreview() {
        Long commentId = 1L, imageId = 100L;
        CommentImage img = buildImage(commentId);
        Resource mockResource = mock(Resource.class);

        when(commentImageRepository.findByIdAndCommentId(imageId, commentId))
                .thenReturn(Optional.of(img));
        when(imageService.download(img.getPreviewKey())).thenReturn(mockResource);

        ImageDownloadDto result = commentImageService.downloadPreviewByCommentId(commentId, imageId);

        assertEquals(img.getFileKey(), result.getOriginalFileName());
        assertEquals(img.getContentType(), result.getContentType());
        assertEquals(mockResource, result.getResource());
    }

    @Test
    void deleteImage_shouldCallDeleteAndRemoveFromDb() {
        Long commentId = 1L, imageId = 100L;
        CommentImage img = buildImage(commentId);

        when(commentImageRepository.findByIdAndCommentId(imageId, commentId))
                .thenReturn(Optional.of(img));

        commentImageService.deleteImage(commentId, imageId);

        verify(imageService).delete(img.getFileKey());
        verify(commentImageRepository).delete(img);
    }

    @Test
    void downloadImage_shouldThrow_whenImageNotFound() {
        when(commentImageRepository.findByIdAndCommentId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () ->
                commentImageService.downloadImageByCommentId(1L, 99L));
    }

    private CommentImage buildImage(Long commentId) {
        return CommentImage.builder()
                .id(100L)
                .commentId(commentId)
                .userId(42L)
                .fileKey("fileKey")
                .previewKey("previewKey")
                .contentType("image/jpeg")
                .size(1234L)
                .build();
    }
}
