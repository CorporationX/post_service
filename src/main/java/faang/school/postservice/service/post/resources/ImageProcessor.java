package faang.school.postservice.service.post.resources;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.FileOperationException;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Setter
@Component
@NoArgsConstructor
public class ImageProcessor {

    @Value("${resource.image.max-rectangle-width}")
    private int maxRectangleWidth;

    @Value("${resource.image.max-rectangle-height}")
    private int maxRectangleHeight;

    @Value("${resource.image.max-square-dimension}")
    private int maxSquareDimension;

    public byte[] processImage(@NonNull MultipartFile file) {
        BufferedImage image = getBufferedImage(file);
        if (checkIfImageNeedsResize(image)) {
            image = resizeImage(image);
        }
        return getImageBytes(image, getImageFormat(file.getOriginalFilename()));
    }

    private BufferedImage getBufferedImage(MultipartFile file) {
        try {
            return Optional.ofNullable(ImageIO.read(file.getInputStream())).orElseThrow(() ->
                    new DataValidationException("Could not read image"));
        } catch (IOException e) {
            throw new FileOperationException(e);
        }
    }

    private boolean checkIfImageNeedsResize(BufferedImage image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        if (Math.max(imageWidth, imageHeight) > maxRectangleWidth) {
            return true;
        }
        if (imageWidth == imageHeight) {
            return false;
        }
        return imageHeight > maxRectangleHeight;
    }

    private BufferedImage resizeImage(BufferedImage image) {
        int targetWidth;
        int targetHeight;
        if (image.getWidth() == image.getHeight()) {
            targetWidth = maxSquareDimension;
            targetHeight = maxSquareDimension;
        } else {
            targetWidth = maxRectangleWidth;
            targetHeight = maxRectangleHeight;
        }
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, image.getType());
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(image, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        return resizedImage;
    }

    private String getImageFormat(String fileName) {
        String[] tokens = fileName.split("\\.");
        return tokens[tokens.length - 1];
    }

    private byte[] getImageBytes(BufferedImage image, String format) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new FileOperationException(e);
        }
    }
}
