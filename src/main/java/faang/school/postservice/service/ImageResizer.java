package faang.school.postservice.service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class ImageResizer {

    public BufferedImage resize(BufferedImage image, int targetWidth, int targetHeight) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(image)
                .size(targetWidth, targetHeight)
                .keepAspectRatio(true)
                .outputFormat("jpg")
                .toOutputStream(outputStream);
        BufferedImage result = ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));
        outputStream.close();
        return result;
    }
}
