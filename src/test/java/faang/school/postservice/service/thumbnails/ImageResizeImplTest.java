package faang.school.postservice.service.thumbnails;

import faang.school.postservice.exception.ImageResizeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageResizeImplTest {

    @Mock
    private ImageResizeProperties imageResizeProperties;
    @InjectMocks
    private ImageResizeImpl imageResizeImpl;

    @Test
    public void testGetResizedImagesThrowException() {
        assertThrows(ImageResizeException.class, () -> imageResizeImpl.getResizedImages(new byte[]{0}));
    }

    @Test
    public void testGetResizedImages() throws IOException {
        BufferedImage image = new BufferedImage(3000, 2000, BufferedImage.TYPE_INT_RGB);
        byte[] imageBytes = convertBufferedImageToBytes(image);

        when(imageResizeProperties.getResizeSizes()).thenReturn(List.of(1080, 170));
        Map<String, byte[]> resizedImages = imageResizeImpl.getResizedImages(imageBytes);

        assertNotNull(resizedImages);
        assertEquals(2, resizedImages.size());
    }

    @Test
    public void testGetResizedImagesWithEmptyReturn() throws IOException {
        BufferedImage image = new BufferedImage(160, 100, BufferedImage.TYPE_INT_RGB);
        byte[] imageBytes = convertBufferedImageToBytes(image);

        when(imageResizeProperties.getResizeSizes()).thenReturn(List.of(1080, 170));
        Map<String, byte[]> resizedImages = imageResizeImpl.getResizedImages(imageBytes);

        assertNotNull(resizedImages);
        assertEquals(0, resizedImages.size());
    }


    private byte[] convertBufferedImageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", outputStream);
        return outputStream.toByteArray();
    }
}
