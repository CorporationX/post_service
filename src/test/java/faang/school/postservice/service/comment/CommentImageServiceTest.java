package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.exception.file.FileNotFoundException;
import faang.school.postservice.model.comment.Comment;
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
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    void uploadImageForComment_shouldSaveImageAndReturnDto() {
        Long commentId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        Comment comment = new Comment();
        ImageResponseDto dto = new ImageResponseDto("fileKey", "previewKey", "image/jpeg", 1234);
        Long userId = 42L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(imageService.uploadToS3(file)).thenReturn(dto);
        when(userContext.getUserId()).thenReturn(userId);

        ImageResponseDto result = commentImageService.uploadImageForComment(commentId, file);

        assertEquals(dto, result);
        verify(commentImageRepository).save(any(CommentImage.class));
    }

    @Test
    void downloadImageByCommentId_shouldReturnDto() throws Exception {
        Long commentId = 1L;
        Long imageId = 2L;
        CommentImage image = CommentImage.builder()
                .id(imageId)
                .fileKey("fileKey")
                .contentType("image/jpeg")
                .build();
        InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3});

        when(commentImageRepository.findByIdAndCommentId(imageId, commentId)).thenReturn(Optional.of(image));
        when(imageService.download("fileKey")).thenReturn(stream);

        ImageDownloadDto result = commentImageService.downloadImageByCommentId(commentId, imageId);

        assertEquals("fileKey", result.getOriginalFileName());
        assertEquals("image/jpeg", result.getContentType());
        assertSame(stream, result.getInputStream());
    }

    @Test
    void deleteImage_shouldDeleteBothFromS3AndRepository() {
        Long commentId = 1L;
        Long imageId = 10L;
        CommentImage image = new CommentImage();
        image.setFileKey("some-key");

        when(commentImageRepository.findByIdAndCommentId(imageId, commentId)).thenReturn(Optional.of(image));

        commentImageService.deleteImage(commentId, imageId);

        verify(imageService).delete("some-key");
        verify(commentImageRepository).delete(image);
    }

    @Test
    void getContentType_shouldReturnParsedType() {
        Long commentId = 1L;
        Long imageId = 2L;
        String contentType = "image/png";
        CommentImage image = new CommentImage();
        image.setFileKey("fileKey");

        when(commentImageRepository.findByIdAndCommentId(imageId, commentId)).thenReturn(Optional.of(image));
        when(imageService.detectContentType("fileKey")).thenReturn(MediaType.parseMediaType(contentType));

        MediaType type = commentImageService.getContentType(commentId, imageId);

        assertEquals(MediaType.IMAGE_PNG, type);
    }

    @Test
    void getCommentImageByIdOrThrow_shouldThrowExceptionIfNotFound() {
        Long commentId = 1L;
        Long imageId = 99L;

        when(commentImageRepository.findByIdAndCommentId(imageId, commentId)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () ->
                commentImageService.downloadImageByCommentId(commentId, imageId));
    }
}