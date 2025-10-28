package faang.school.postservice.service.resource;

import faang.school.postservice.exceptions.FileException;
import faang.school.postservice.model.ImageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageProcessService {

    @Value("${services.s3.posts.image.horizontal.max-width}")
    private int HORIZONTAL_MAX_WIDTH;

    @Value("${services.s3.posts.image.horizontal.max-height}")
    private int HORIZONTAL_MAX_HEIGHT;

    @Value("${services.s3.posts.image.square.max-width}")
    private int SQUARE_MAX_WIDTH;

    @Value("${services.s3.posts.image.square.max-height}")
    private int SQUARE_MAX_HEIGHT;

    public byte[] resizeImage(MultipartFile originalFile, ImageType imageType) {
        try {
            BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());
            if (originalImage == null) {
                return originalFile.getBytes();
            }

            int maxWidth = getMaxWidth(imageType);
            int maxHeight = getMaxHeight(imageType);

            if (originalImage.getWidth() <= maxWidth && originalImage.getHeight() <= maxHeight) {
                return originalFile.getBytes();
            }
            double ratio = Math.min(
                    (double) maxWidth / originalImage.getWidth(),
                    (double) maxHeight / originalImage.getHeight()
            );

            int newWidth = (int) (originalImage.getWidth() * ratio);
            int newHeight = (int) (originalImage.getHeight() * ratio);

            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", byteStream);
            return byteStream.toByteArray();

        } catch (IOException e) {
            log.warn("Error resizing image");
            throw new FileException("There was an error loading the image");
        }
    }

    private int getMaxWidth(ImageType type) {
        return type == ImageType.HORIZONTAL ? HORIZONTAL_MAX_WIDTH : SQUARE_MAX_WIDTH;
    }

    private int getMaxHeight(ImageType type) {
        return type == ImageType.HORIZONTAL ? HORIZONTAL_MAX_HEIGHT : SQUARE_MAX_HEIGHT;
    }
}