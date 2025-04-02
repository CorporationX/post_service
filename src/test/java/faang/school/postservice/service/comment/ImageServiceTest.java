package faang.school.postservice.service.comment;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.exception.ImageProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {
    @InjectMocks
    private ImageService imageService;

    @Mock
    private AmazonS3 amazonS3;

    @Test
    void validateImageSuccessJpg() throws Exception {
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", imageBytes);

        assertDoesNotThrow(() -> imageService.uploadResizedImages(file, 1L));
    }

    @Test
    void validateImageFailsIfTooBig() {
        byte[] tooBig = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile file = new MockMultipartFile(
                "image", "big.jpg", "image/jpeg", tooBig);

        ImageProcessingException ex = assertThrows(ImageProcessingException.class,
                () -> imageService.uploadResizedImages(file, 1L));

        assertTrue(ex.getMessage().contains("Image size must not exceed"));
    }

    @Test
    void validateImageFailsIfNotImageType() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "text.txt", "text/plain", "hello".getBytes());

        ImageProcessingException ex = assertThrows(ImageProcessingException.class,
                () -> imageService.uploadResizedImages(file, 1L));

        assertTrue(ex.getMessage().contains("Invalid file type: not an image"));
    }

    @Test
    void validateImageFailsIfUnsupportedFormat() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "anim.gif", "image/gif", new byte[500]);

        ImageProcessingException ex = assertThrows(ImageProcessingException.class,
                () -> imageService.uploadResizedImages(file, 1L));

        assertTrue(ex.getMessage().contains("Only JPG and PNG formats are supported"));
    }
}
