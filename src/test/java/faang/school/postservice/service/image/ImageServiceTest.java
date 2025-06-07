package faang.school.postservice.service.image;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.model.CommentImage;
import faang.school.postservice.repository.comment.CommentImageRepository;
import faang.school.postservice.service.s3.S3KeyGenerator;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.validation.image.ImageFileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    private static final Long IMAGE_ID = 1L;
    private static final String FILE_KEY = "img.jpg";
    private static final String PREVIEW_KEY = "small/img.jpg";

    @Mock
    private CommentImageRepository imageRepository;
    
    @Mock
    private ImageFileValidator imageFileValidator;

    @Mock
    private S3Service s3Service;

    @Mock
    private S3KeyGenerator s3KeyGenerator;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ImageService imageService;

    private CommentImage testImage;

    @BeforeEach
    void setUp() {
        testImage = CommentImage.builder()
                .fileKey(FILE_KEY)
                .previewKey(PREVIEW_KEY)
                .contentType("image/jpeg")
                .size(123L)
                .userId(42L)
                .build();
    }

    @Test
    void uploadImage_shouldStoreAndReturnMetadata() throws Exception {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("img.jpg");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("img".getBytes()));
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(123L);
        when(userContext.getUserId()).thenReturn(42L);
        when(s3KeyGenerator.generateImageKey(any())).thenReturn(FILE_KEY);
        when(s3KeyGenerator.generatePreviewKey(any())).thenReturn(PREVIEW_KEY);
        when(imageRepository.save(any())).thenReturn(testImage);

        ImageResponseDto dto = imageService.uploadImage(file);

        assertEquals(FILE_KEY, dto.getFileKey());
        assertEquals(PREVIEW_KEY, dto.getPreviewKey());
        assertEquals("image/jpeg", dto.getContentType());
        assertEquals(123L, dto.getSize());
    }

    @Test
    void downloadImageById_shouldReturnStream() {
        when(imageRepository.getByIdOrThrow(IMAGE_ID)).thenReturn(testImage);
        when(s3Service.download(FILE_KEY)).thenReturn(new ByteArrayInputStream("data".getBytes()));

        var dto = imageService.downloadImageById(IMAGE_ID);

        assertEquals(FILE_KEY, dto.getOriginalFileName());
        assertEquals("image/jpeg", dto.getContentType());
    }

    @Test
    void deleteImage_shouldCallS3AndRepo() {
        when(imageRepository.getByIdOrThrow(IMAGE_ID)).thenReturn(testImage);

        imageService.deleteImage(IMAGE_ID);

        verify(s3Service).delete(FILE_KEY);
    }
}
