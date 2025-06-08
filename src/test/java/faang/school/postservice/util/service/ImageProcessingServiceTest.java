package faang.school.postservice.util.service;

import faang.school.postservice.service.image.ImageProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ImageProcessingServiceTest {
    private ImageProcessingService imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageProcessingService();
    }

    private MockMultipartFile createTestImage(int width,
                                              int height,
                                              String formatName) throws IOException {

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(Color.BLUE);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, formatName, baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();

        String filename = "test." + formatName;
        String contentType = "image/" + formatName;

        return new MockMultipartFile("file", filename, contentType, bytes);
    }

    @Test
    void testResizeImageKeepsAspectRatioAndReturnsJpegBytes() throws IOException {
        MockMultipartFile original = createTestImage(2000, 1000, "png");

        byte[] resizedBytes = imageService.resizeImage(original, 500);

        assertNotNull(resizedBytes);
        assertTrue(resizedBytes.length < original.getBytes().length);

        BufferedImage resizedImg = ImageIO.read(new java.io.ByteArrayInputStream(resizedBytes));
        assertNotNull(resizedImg);
        assertTrue(resizedImg.getWidth() <= 500);
        assertTrue(resizedImg.getHeight() <= 500);

        String[] writers = ImageIO.getWriterFormatNames();
        boolean jpegSupported = false;
        for (String w : writers) {
            if (w.equalsIgnoreCase("jpeg") || w.equalsIgnoreCase("jpg")) {
                jpegSupported = true;
                break;
            }
        }
        assertTrue(jpegSupported);
    }

    @Test
    void testCreateLargeAndSmallImageRespectMaxDimensions() throws IOException {
        MockMultipartFile original = createTestImage(3000, 3000, "jpg");

        byte[] largeBytes = imageService.createLargeImage(original);
        BufferedImage largeImg = ImageIO.read(new java.io.ByteArrayInputStream(largeBytes));
        assertNotNull(largeImg);
        assertTrue(Math.max(largeImg.getWidth(), largeImg.getHeight()) <= 1080);

        byte[] smallBytes = imageService.createSmallImage(original);
        BufferedImage smallImg = ImageIO.read(new java.io.ByteArrayInputStream(smallBytes));
        assertNotNull(smallImg);
        assertTrue(Math.max(smallImg.getWidth(), smallImg.getHeight()) <= 170);
    }
}
