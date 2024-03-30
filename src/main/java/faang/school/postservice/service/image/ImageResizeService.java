package faang.school.postservice.service.image;

import faang.school.postservice.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Slf4j
@Component
public class ImageResizeService {

    @Value("${image.horizontalWidth}")
    private Integer horizontalImageWidth;

    @Value("${image.horizontalHeight}")
    private Integer horizontalImageHeight;

    @Value("${image.squareSize}")
    private Integer squareImageSize;

    public BufferedImage getResizedImage(MultipartFile picture) {
        try {
            BufferedImage image = ImageIO.read(picture.getInputStream());
            if (image.getWidth() == image.getHeight()) {
                if (image.getHeight() > squareImageSize) {
                    image = Scalr.resize(image, image.getWidth(), squareImageSize);
                }
                if (image.getWidth() > squareImageSize) {
                    image = Scalr.resize(image, squareImageSize, image.getHeight());
                }
                return image;
            }
            if (image.getWidth() > horizontalImageWidth) {
                image = Scalr.resize(image, horizontalImageWidth, image.getHeight());
            }
            if (image.getHeight() > horizontalImageHeight) {
                image = Scalr.resize(image, image.getWidth(), horizontalImageHeight);
            }
            return image;
        } catch (IOException e) {
            log.error("FileException", e);
            throw new FileException(e.getMessage());
        }
    }
}
