package faang.school.postservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import faang.school.postservice.exception.FileFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@ExtendWith(MockitoExtension.class)
class ImageProcessorTest {

    @InjectMocks
    private ImageProcessor imageProcessor;

    @Mock
    private MultipartFile mockFile;

    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        testImage = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = testImage.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 200, 100);
        g2d.dispose();
    }

    @Test
    void testResizeImage() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(testImage, "png", baos);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

        when(mockFile.getInputStream()).thenReturn(inputStream);

        BufferedImage resizedImage = imageProcessor.resizeImage(mockFile, 100);

        assertNotNull(resizedImage);
        assertEquals(100, resizedImage.getWidth());
        assertTrue(resizedImage.getHeight() > 0);
    }

    @Test
    void testResizeImage_InvalidFileFormat() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);

        when(mockFile.getInputStream()).thenReturn(inputStream);

        assertThrows(FileFormatException.class, () -> imageProcessor.resizeImage(mockFile, 100));
    }

    @Test
    void testBufferedImageToByteArray_ValidFormat() throws IOException {
        byte[] imageBytes = imageProcessor.bufferedImageToByteArray(testImage, "png");

        assertNotNull(imageBytes, "Returned byte array should not be null");
        assertTrue(imageBytes.length > 0, "Returned byte array should not be empty");
    }

}

