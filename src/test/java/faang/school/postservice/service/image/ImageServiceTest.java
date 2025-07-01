package faang.school.postservice.service.image;

import faang.school.postservice.dto.image.ImageResource;
import faang.school.postservice.exception.file.FileReadException;
import faang.school.postservice.service.s3.S3KeyGenerator;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.validation.image.ImageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    private static final String TEST_IMAGE_PATH = "test-images/kik.jpg";
    private static final String TEST_IMAGE_NAME = "kik.jpg";
    private static final String TEST_IMAGE_TYPE = "image/jpeg";
    private static final String IMAGE_KEY = "image-key";
    private static final String PREVIEW_KEY = "preview-key";
    private static final String DOWNLOAD_KEY = "key";

    @Mock
    private ImageValidator imageValidator;
    @Mock
    private S3Service s3Service;
    @Mock
    private S3KeyGenerator s3KeyGenerator;

    @InjectMocks
    private ImageService imageService;

    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() throws IOException {
        try (InputStream is = new ClassPathResource(TEST_IMAGE_PATH).getInputStream()) {
            byte[] content = is.readAllBytes();
            mockFile = new MockMultipartFile("file", TEST_IMAGE_NAME, TEST_IMAGE_TYPE, content);
        }
    }

    @Test
    void shouldUploadImageAndReturnImageResource() {
        when(s3KeyGenerator.generateImageKey(TEST_IMAGE_NAME)).thenReturn(IMAGE_KEY);
        when(s3KeyGenerator.generatePreviewKey(IMAGE_KEY)).thenReturn(PREVIEW_KEY);

        BufferedImage dummyImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        try (MockedStatic<ImageIO> imageIO = ImageIOStubber.stubImageIORead(dummyImage)) {
            ImageResource result = imageService.uploadToS3(mockFile);

            assertEquals(IMAGE_KEY, result.fileKey());
            assertEquals(PREVIEW_KEY, result.previewKey());
            assertEquals(TEST_IMAGE_TYPE, result.contentType());
            assertEquals(mockFile.getSize(), result.size());

            verify(s3Service, times(2)).upload(any(byte[].class), anyString(), eq(TEST_IMAGE_TYPE));
        }
    }

    @Test
    void shouldThrowExceptionWhenUploadedFileIsNotImage() {
        when(s3KeyGenerator.generateImageKey(anyString())).thenReturn(IMAGE_KEY);
        when(s3KeyGenerator.generatePreviewKey(anyString())).thenReturn(PREVIEW_KEY);

        try (MockedStatic<ImageIO> imageIO = ImageIOStubber.stubImageIORead(null)) {
            assertThrows(FileReadException.class, () -> imageService.uploadToS3(mockFile));
        }
    }

    @Test
    void shouldDownloadImageFromS3() {
        Resource expected = new ByteArrayResource(new byte[]{1, 2, 3});
        when(s3Service.download(DOWNLOAD_KEY)).thenReturn(expected);

        Resource actual = imageService.download(DOWNLOAD_KEY);

        assertEquals(expected, actual);
    }

    @Test
    void shouldDeleteImageFromS3() {
        imageService.delete(DOWNLOAD_KEY);

        verify(s3Service).delete(DOWNLOAD_KEY);
    }
}

class ImageIOStubber {
    public static MockedStatic<ImageIO> stubImageIORead(BufferedImage image) {
        MockedStatic<ImageIO> mock = Mockito.mockStatic(ImageIO.class, CALLS_REAL_METHODS);
        mock.when(() -> ImageIO.read(any(InputStream.class))).thenReturn(image);
        return mock;
    }
}
