package faang.school.postservice.service.post.resources;

import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImageProcessorTest {
    private static final int MAX_RECTANGLE_WIDTH = 1080;
    private static final int MAX_RECTANGLE_HEIGHT = 566;
    private static final int MAX_SQUARE_DIMENSION = 1080;
    private static final String CONTENT_TYPE_JPEG = "image/jpeg";

    private ImageProcessor imageProcessor;

    @BeforeEach
    void setUp() {
        imageProcessor = new ImageProcessor();
        imageProcessor.setMaxRectangleWidth(MAX_RECTANGLE_WIDTH);
        imageProcessor.setMaxRectangleHeight(MAX_RECTANGLE_HEIGHT);
        imageProcessor.setMaxSquareDimension(MAX_SQUARE_DIMENSION);
    }

    @Test
    @DisplayName("Process rectangle image")
    void imageProcessorTest_processRectangleImage() {
        MultipartFile imageFile = initImageFile("test.jpeg", CONTENT_TYPE_JPEG, 700, 500);
        try {
            byte[] expected = imageFile.getBytes();

            byte[] result = imageProcessor.processImage(imageFile);

            assertArrayEquals(expected, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Process square image")
    void imageProcessorTest_processSquareImage() {
        MultipartFile imageFile = initImageFile("test.jpeg", CONTENT_TYPE_JPEG, 500, 500);
        try {
            byte[] expected = imageFile.getBytes();

            byte[] result = imageProcessor.processImage(imageFile);

            assertArrayEquals(expected, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Process rectangle image with resize on width")
    void imageProcessorTest_processRectangleImageWithResizeOnWidth() {
        MultipartFile imageFile = initImageFile("test.jpeg", CONTENT_TYPE_JPEG, 1500, 500);
        try {
            byte[] result = imageProcessor.processImage(imageFile);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(result));

            assertEquals(MAX_RECTANGLE_WIDTH, image.getWidth());
            assertEquals(MAX_RECTANGLE_HEIGHT, image.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Process rectangle image with resize on height")
    void imageProcessorTest_processRectangleImageWithResizeOnHeight() {
        MultipartFile imageFile = initImageFile("test.jpeg", CONTENT_TYPE_JPEG, 500, 800);
        try {
            byte[] result = imageProcessor.processImage(imageFile);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(result));

            assertEquals(MAX_RECTANGLE_WIDTH, image.getWidth());
            assertEquals(MAX_RECTANGLE_HEIGHT, image.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Process square image with resize")
    void imageProcessorTest_processSquareImageWithResize() {
        MultipartFile imageFile = initImageFile("test.jpeg", CONTENT_TYPE_JPEG, 1500, 1500);
        try {
            byte[] result = imageProcessor.processImage(imageFile);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(result));

            assertEquals(MAX_SQUARE_DIMENSION, image.getWidth());
            assertEquals(MAX_SQUARE_DIMENSION, image.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Process not an image")
    void imageProcessorTest_processNotAnImage() {
        byte[] fileContent = new byte[1000];
        MultipartFile file = new MockMultipartFile(
                "test", "test.jpeg",
                CONTENT_TYPE_JPEG, fileContent);

        assertThrows(DataValidationException.class, () -> imageProcessor.processImage(file));
    }

    @Test
    @DisplayName("Process image with null file")
    void imageProcessorTest_processImageWithNullFile() {
        String expectedMessage = "file is marked non-null but is null";

        NullPointerException ex =
                assertThrows(NullPointerException.class, () -> imageProcessor.processImage(null));

        assertEquals(expectedMessage, ex.getMessage());
    }

    private MultipartFile initImageFile(String fileName, String contentType, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, contentType.split("/")[1], baos);
            InputStream imageStream = new ByteArrayInputStream(baos.toByteArray());
            return new MockMultipartFile("test", fileName, contentType, imageStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
