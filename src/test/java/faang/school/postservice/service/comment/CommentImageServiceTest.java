package faang.school.postservice.service.comment;

import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.dto.image.ImageStorage;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.model.ImageResource;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.model.comment.CommentImage;
import faang.school.postservice.repository.comment.CommentImageRepository;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentImageServiceTest {

    private static final String TEST_IMAGE_PATH = "test-images/kik.jpg";
    private static final String TEST_IMAGE_NAME = "kik.jpg";
    private static final String TEST_IMAGE_TYPE = "image/jpeg";
    private final Long COMMENT_ID = 1L;
    private final Long IMAGE_ID = 100L;


    @Mock
    private ImageService imageService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentImageRepository commentImageRepository;

    @InjectMocks
    private CommentImageService commentImageService;

    private MultipartFile file;
    private Comment comment;
    private ImageStorage imageStorage;
    private ImageResource imageResource;
    private ImageResource previewResource;
    private CommentImage commentImage;

    @BeforeEach
    void setUp() throws IOException {
        ClassPathResource resource = new ClassPathResource(TEST_IMAGE_PATH);
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());

        file = new MockMultipartFile(
                "file",
                TEST_IMAGE_NAME,
                TEST_IMAGE_TYPE,
                bytes
        );
        file = new MockMultipartFile("file", TEST_IMAGE_NAME, TEST_IMAGE_TYPE, new byte[]{1, 2, 3});

        comment = Comment.builder().id(COMMENT_ID).authorId(42L).build();

        imageStorage = new ImageStorage("file-key", "preview-key", TEST_IMAGE_TYPE, 123L);

        imageResource = ImageResource.builder().id(10L).key("file-key").name(TEST_IMAGE_NAME).type(TEST_IMAGE_TYPE).size(123L).build();
        previewResource = ImageResource.builder().id(11L).key("preview-key").name("preview_test.jpg").type(TEST_IMAGE_TYPE).size(123L).build();

        commentImage = CommentImage.builder()
                .id(IMAGE_ID)
                .comment(comment)
                .image(imageResource)
                .preview(previewResource)
                .build();
    }

    @Test
    void uploadImageForComment_shouldUploadAndReturnDto() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(imageService.uploadToS3(file)).thenReturn(imageStorage);
        when(resourceService.saveResource(any())).thenReturn(imageResource, previewResource);
        when(commentImageRepository.save(any())).thenReturn(commentImage);

        ImageResponseDto dto = commentImageService.uploadImageForComment(COMMENT_ID, file);

        assertEquals("file-key", dto.getFileKey());
        assertEquals("preview-key", dto.getPreviewKey());
        assertEquals(TEST_IMAGE_TYPE, dto.getContentType());
        assertEquals(123L, dto.getSize());

        verify(commentValidator).validateCommentAuthor(42L);
    }

    @Test
    void downloadImageByCommentId_shouldReturnDownloadDto() {
        when(commentImageRepository.findByIdAndCommentId(IMAGE_ID, COMMENT_ID)).thenReturn(Optional.of(commentImage));
        when(imageService.download("file-key")).thenReturn(new ByteArrayResource(new byte[]{1}));

        ImageDownloadDto dto = commentImageService.downloadImageByCommentId(COMMENT_ID, IMAGE_ID);

        assertEquals(IMAGE_ID, dto.getImageId());
        assertEquals(TEST_IMAGE_NAME, dto.getOriginalFileName());
        assertEquals(MediaType.IMAGE_JPEG, dto.getContentType());
        assertNotNull(dto.getResource());
    }

    @Test
    void downloadPreviewByCommentId_shouldReturnDownloadDto() {
        when(commentImageRepository.findByIdAndCommentId(IMAGE_ID, COMMENT_ID)).thenReturn(Optional.of(commentImage));
        when(imageService.download("preview-key")).thenReturn(new ByteArrayResource(new byte[]{1}));

        ImageDownloadDto dto = commentImageService.downloadPreviewByCommentId(COMMENT_ID, IMAGE_ID);

        assertEquals(IMAGE_ID, dto.getImageId());
        assertEquals("preview_test.jpg", dto.getOriginalFileName());
        assertEquals(MediaType.IMAGE_JPEG, dto.getContentType());
    }

    @Test
    void deleteImage_shouldDeleteImageAndResources() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(commentImageRepository.findByIdAndCommentId(IMAGE_ID, COMMENT_ID)).thenReturn(Optional.of(commentImage));

        commentImageService.deleteImage(COMMENT_ID, IMAGE_ID);

        verify(commentValidator).validateCommentAuthor(42L);
        verify(imageService).delete("file-key");
        verify(imageService).delete("preview-key");
        verify(commentImageRepository).delete(commentImage);
        verify(resourceService).deleteResource(imageResource);
        verify(resourceService).deleteResource(previewResource);
    }

    @Test
    void uploadImageForComment_shouldThrowIfCommentNotFound() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentImageService.uploadImageForComment(COMMENT_ID, file));
    }
}
