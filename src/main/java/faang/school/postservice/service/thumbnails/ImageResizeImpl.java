package faang.school.postservice.service.thumbnails;

import faang.school.postservice.exception.ImageResizeException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageResizeImpl implements ImageResize {

    private final ImageResizeProperties imageResizeProperties;

    @NotNull
    @Override
    public Map<String, byte[]> getResizedImages(byte[] imageBytes) {
        Map<String, byte[]> resultMap = new HashMap<>();

        BufferedImage bufferedImage = getBufferedImageFromBytes(imageBytes);
        int maxSide = Math.max(bufferedImage.getWidth(), bufferedImage.getHeight());

        imageResizeProperties.getResizeSizes().stream()
                .filter(resizeSize -> resizeSize < maxSide)
                .forEach(resizeSize ->
                        resultMap.put(resizeSize.toString(), resizeImage(bufferedImage, resizeSize, maxSide)));

        log.debug("Resized image size: {}", resultMap.size());
        return resultMap;
    }

    private byte[] resizeImage(BufferedImage bufferedImage, Integer targetSize, int maxSide) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        double scale = (double) targetSize / maxSide;
        try {
            Thumbnails.of(bufferedImage)
                    .scale(scale)
                    .outputFormat("jpeg")
                    .toOutputStream(outputStream);
        } catch (IOException e) {
            log.error("Error while resizing image", e);
            throw new ImageResizeException(e);
        }

        return outputStream.toByteArray();
    }

    private BufferedImage getBufferedImageFromBytes(byte[] imageBytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            return ImageIO.read(bais);
        } catch (IOException e) {
            log.error("Error while reading image", e);
            throw new ImageResizeException(e);
        }
    }
}
