package faang.school.postservice.service;

import faang.school.postservice.config.resource.ResourceProperties;
import faang.school.postservice.service.image.CustomMultipartFile;
import faang.school.postservice.service.image.ImageProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageProcessorTest {

    @Mock
    private ResourceProperties resourceProperties;

    private ImageProcessor imageProcessor;

    @BeforeEach
    void setUp() {
        imageProcessor = new ImageProcessor(resourceProperties);
    }

    private MultipartFile createTestImage(int width, int height, String filename, String contentType) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageData = baos.toByteArray();

        return new CustomMultipartFile(filename, contentType, imageData);
    }

    @Test
    void compressImage_Success() throws Exception {
        when(resourceProperties.getImageMaxWidth()).thenReturn(1920);
        when(resourceProperties.getImageMaxHeight()).thenReturn(1080);
        MultipartFile originalFile = createTestImage(2000, 2000, "test.jpg", "image/jpeg");

        MultipartFile result = imageProcessor.compressImage(originalFile);

        assertThat(result).isNotSameAs(originalFile);
        assertThat(result.getOriginalFilename()).isEqualTo(originalFile.getOriginalFilename());
        assertThat(result.getContentType()).isEqualTo(originalFile.getContentType());
        assertThat(result.getSize()).isLessThan(originalFile.getSize());
    }

    @Test
    void compressImage_ReturnsOriginalWhenUnsupportedFormat() throws Exception {
        MultipartFile originalFile = mock(MultipartFile.class);
        when(originalFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(originalFile.getOriginalFilename()).thenReturn("test.bmp");

        MultipartFile result = imageProcessor.compressImage(originalFile);

        assertThat(result).isSameAs(originalFile);
    }

    @Test
    void compressImage_ReturnsOriginalWhenExceptionDuringProcessing() throws Exception {
        MultipartFile originalFile = mock(MultipartFile.class);
        when(originalFile.getInputStream()).thenThrow(IOException.class);
        when(originalFile.getOriginalFilename()).thenReturn("test.jpg");

        MultipartFile result = imageProcessor.compressImage(originalFile);

        assertThat(result).isSameAs(originalFile);
    }

    @Test
    void needsCompression_ReturnsTrueWhenImageTooLarge() throws Exception {
        when(resourceProperties.getImageMaxWidth()).thenReturn(1920);
        MultipartFile largeImage = createTestImage(3000, 2000, "large.jpg", "image/jpeg");

        boolean result = imageProcessor.needsCompression(largeImage);

        assertThat(result).isTrue();
    }

    @Test
    void needsCompression_ReturnsFalseWhenImageWithinLimits() throws Exception {
        when(resourceProperties.getImageMaxWidth()).thenReturn(1920);
        when(resourceProperties.getImageMaxHeight()).thenReturn(1080);
        MultipartFile smallImage = createTestImage(500, 500, "small.jpg", "image/jpeg");

        boolean result = imageProcessor.needsCompression(smallImage);

        assertThat(result).isFalse();
    }

    @Test
    void needsCompression_ReturnsFalseWhenUnreadableFile() throws Exception {
        MultipartFile corruptFile = mock(MultipartFile.class);
        when(corruptFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        boolean result = imageProcessor.needsCompression(corruptFile);

        assertThat(result).isFalse();
    }

    @Test
    void getFormatName_VariousContentTypes_ReturnsCorrectFormat() {
        assertThat(imageProcessor.getFormatName("image/png")).isEqualTo("png");
        assertThat(imageProcessor.getFormatName("image/jpeg")).isEqualTo("jpg");
        assertThat(imageProcessor.getFormatName("image/gif")).isEqualTo("gif");
        assertThat(imageProcessor.getFormatName(null)).isEqualTo("jpg");
        assertThat(imageProcessor.getFormatName("unknown")).isEqualTo("jpg");
    }
}
