package faang.school.postservice.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class ImageService {
    public byte[] resizeImage(MultipartFile image) {
        try {
            InputStream inputStream = image.getInputStream();
            BufferedImage originalImage = ImageIO.read(inputStream);

            int height = originalImage.getHeight();
            int width = originalImage.getWidth();

            if (height > 1080 || width > 1080) {
                if (height == width) {
                    height = 1080;
                    width = 1080;
                } else if (height > width) {
                    height = 1080;
                    width = (1080 * width)/height;
                } else {
                    height = (1080 * height)/width;
                    width = 1080;
                }
            }

            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(originalImage, 0, 0, width, height, null);
            graphics2D.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error resizing image: " + e.getMessage());
        }
    }
}
