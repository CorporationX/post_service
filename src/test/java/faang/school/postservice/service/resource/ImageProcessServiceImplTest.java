package faang.school.postservice.service.resource;

import faang.school.postservice.exceptions.FileException;
import faang.school.postservice.model.ImageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageProcessServiceImplTest {

    @InjectMocks
    private ImageProcessServiceImpl imageProcessService;

    @BeforeEach
    void setUp() {
        setField(imageProcessService, "HORIZONTAL_MAX_WIDTH", 1920);
        setField(imageProcessService, "HORIZONTAL_MAX_HEIGHT", 1080);
        setField(imageProcessService, "SQUARE_MAX_WIDTH", 1080);
        setField(imageProcessService, "SQUARE_MAX_HEIGHT", 1080);
    }


    @Test
    void resizeImage_smallImageNoResizeNeeded_shouldReturnOriginalBytes() {
        BufferedImage smallImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(smallImage, "jpg", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] imageBytes = baos.toByteArray();

        var file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                imageBytes
        );

        byte[] result = imageProcessService.resizeImage(file, ImageType.HORIZONTAL);

        assertNotNull(result);
    }

    @Test
    void resizeImage_largeImageHorizontal_shouldResize()  {
        BufferedImage largeImage = new BufferedImage(3000, 2000, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(largeImage, "jpg", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] imageBytes = baos.toByteArray();

        var file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                imageBytes
        );

        byte[] result = imageProcessService.resizeImage(file, ImageType.HORIZONTAL);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void resizeImage_largeImageSquare_shouldResize()  {
        BufferedImage largeImage = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(largeImage, "jpg", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] imageBytes = baos.toByteArray();

        var file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                imageBytes
        );

        byte[] result = imageProcessService.resizeImage(file, ImageType.SQUARE);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void resizeImage_ioException_shouldThrowFileException(){
        var file = mock(org.springframework.web.multipart.MultipartFile.class);
        try {
            when(file.getInputStream()).thenThrow(new IOException("Test error"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertThrows(FileException.class,
                () -> imageProcessService.resizeImage(file, ImageType.HORIZONTAL));
    }

    @Test
    void resizeImage_ioExceptionOnGetBytes_shouldThrowFileException() {
        var file = mock(org.springframework.web.multipart.MultipartFile.class);
        try {
            when(file.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("test".getBytes()));
            when(file.getBytes()).thenThrow(new IOException("Bytes error"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertThrows(FileException.class,
                () -> imageProcessService.resizeImage(file, ImageType.HORIZONTAL));
    }

    private void setField(Object target, String fieldName, Object value) {
        Field field;
        try {
            field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException| IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}