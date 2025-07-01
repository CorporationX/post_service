package faang.school.postservice.service.comment;

import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.image.ImageResource;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.entity.resource.Resource;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.model.resource.ImageResources;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.service.image.ImageService;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validation.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentImageServiceTest {

    private static final String TEST_IMAGE_NAME = "kik.jpg";
    private static final String PREVIEW_IMAGE_NAME = "preview_test.jpg";
    private static final String TEST_IMAGE_TYPE = "image/jpeg";
    private static final Long COMMENT_ID = 1L;
    private static final Long AUTHOR_ID = 42L;

    @Mock
    private ImageService imageService;
    @Mock
    private ResourceService resourceService;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentImageService commentImageService;

    private MultipartFile file;
    private Comment comment;
    private ImageResource imageStorage;
    private Resource originalResource;
    private Resource previewResource;

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile("file", TEST_IMAGE_NAME, TEST_IMAGE_TYPE, new byte[]{1, 2, 3});

        comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setAuthorId(AUTHOR_ID);

        originalResource = new Resource();
        originalResource.setId(10L);
        originalResource.setKey("file-key");
        originalResource.setName(TEST_IMAGE_NAME);
        originalResource.setType(TEST_IMAGE_TYPE);
        originalResource.setSize(123L);

        previewResource = new Resource();
        previewResource.setId(11L);
        previewResource.setKey("preview-key");
        previewResource.setName(PREVIEW_IMAGE_NAME);
        previewResource.setType(TEST_IMAGE_TYPE);
        previewResource.setSize(123L);

        imageStorage = new ImageResource(TEST_IMAGE_NAME, "file-key", "preview-key", TEST_IMAGE_TYPE, 123L);
    }

    @Test
    void shouldUploadImageForComment() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(imageService.uploadToS3(file)).thenReturn(imageStorage);
        when(resourceService.uploadImageResources(imageStorage)).thenReturn(new ImageResources(originalResource, previewResource));

        ImageResponseDto result = commentImageService.uploadImageForComment(COMMENT_ID, file);

        assertEquals("file-key", result.getFileKey());
        assertEquals("preview-key", result.getPreviewKey());
        assertEquals(TEST_IMAGE_TYPE, result.getContentType());
        assertEquals(123L, result.getSize());

        verify(commentValidator).validateCommentAuthor(AUTHOR_ID);
        verify(commentRepository).save(comment);
    }

    @Test
    void shouldThrowIfCommentNotFoundOnUpload() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class,
                () -> commentImageService.uploadImageForComment(COMMENT_ID, file));
    }

    @Test
    void shouldDownloadImageByCommentId() {
        comment.setLargeImageResource(originalResource);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(imageService.download("file-key")).thenReturn(new ByteArrayResource(new byte[]{1, 2}));

        ImageDownloadDto dto = commentImageService.downloadImageByCommentId(COMMENT_ID);

        assertEquals(TEST_IMAGE_NAME, dto.getOriginalFileName());
        assertEquals(MediaType.IMAGE_JPEG, dto.getContentType());
        assertNotNull(dto.getResource());
    }

    @Test
    void shouldDownloadPreviewByCommentId() {
        comment.setSmallImageResource(previewResource);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(imageService.download("preview-key")).thenReturn(new ByteArrayResource(new byte[]{5}));

        ImageDownloadDto dto = commentImageService.downloadPreviewByCommentId(COMMENT_ID);

        assertEquals(PREVIEW_IMAGE_NAME, dto.getOriginalFileName());
        assertEquals(MediaType.IMAGE_JPEG, dto.getContentType());
        assertNotNull(dto.getResource());
    }

    @Test
    void shouldDeleteImage() {
        comment.setLargeImageResource(originalResource);
        comment.setSmallImageResource(previewResource);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentImageService.deleteImage(COMMENT_ID, 999L); // imageId не используется

        verify(commentValidator).validateCommentAuthor(AUTHOR_ID);
        verify(imageService).delete("file-key");
        verify(imageService).delete("preview-key");
        verify(resourceService).deleteResource(originalResource);
        verify(resourceService).deleteResource(previewResource);
    }

    @Test
    void shouldThrowIfCommentNotFoundOnDelete() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class,
                () -> commentImageService.deleteImage(COMMENT_ID, 123L));
    }

    @Test
    void shouldThrowIfCommentNotFoundOnDownloadImage() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class,
                () -> commentImageService.downloadImageByCommentId(COMMENT_ID));
    }

    @Test
    void shouldThrowIfCommentNotFoundOnDownloadPreview() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class,
                () -> commentImageService.downloadPreviewByCommentId(COMMENT_ID));
    }
}
