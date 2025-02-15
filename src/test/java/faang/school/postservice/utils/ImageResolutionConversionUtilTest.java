package faang.school.postservice.utils;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.exception.UnsupportedResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageResolutionConversionUtilTest {
    private ImageResolutionConversionUtil imageResolutionConversionUtil;

    @BeforeEach
    void setUp() {
        imageResolutionConversionUtil = new ImageResolutionConversionUtil();
    }

    @Test
    void compressImage_shouldCompressValidImage() throws Exception {
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(testImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                new ByteArrayInputStream(imageBytes)
        );

        MultipartFile compressedFile = imageResolutionConversionUtil.compressImage(file);

        assertNotNull(compressedFile);
        assertEquals("test.png", compressedFile.getOriginalFilename());
        assertEquals("image/png", compressedFile.getContentType());
    }

    @Test
    void compressImage_shouldThrowUnsupportedResourceExceptionForNonImage() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));
        when(file.getOriginalFilename()).thenReturn("test.txt");

        UnsupportedResourceException exception = assertThrows(UnsupportedResourceException.class,
                () -> imageResolutionConversionUtil.compressImage(file));

        assertEquals("File is not an image: test.txt", exception.getMessage());
    }

    @Test
    void compressImage_shouldThrowFileExceptionOnIOException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Mocked IO Exception"));

        FileException exception = assertThrows(FileException.class, () -> imageResolutionConversionUtil.compressImage(file));
        assertEquals("Error reading file: Mocked IO Exception", exception.getMessage());
    }

    @Test
    void compressImage_shouldThrowIllegalArgumentExceptionOnExtensionError() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("invalid/mime");
        when(file.getOriginalFilename()).thenReturn("test.invalid");

        assertThrows(IllegalArgumentException.class, () -> imageResolutionConversionUtil.compressImage(file));
    }
}
