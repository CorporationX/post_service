package faang.school.postservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class ImageCompressor {

    public void compressImage(BufferedImage originalImage, MultipartFile file) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int newWidth = width;
        int newHeight = height;

        if (width > 1080 && height <= 566) { // Горизонтальное изображение
            newWidth = 1080;
            newHeight = (1080 * height) / width;
        } else if (width > 1080 || height > 1080) { // Квадратное или любое изображение больше чем 1080x1080
            newWidth = 1080;
            newHeight = (1080 * height) / width;
            if (newHeight > 1080) {
                newHeight = 1080;
                newWidth = (1080 * width) / height;
            }
        }

        Image tmp = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        try {
            ImageIO.write(resizedImage, file.getContentType(), baos);
            baos.flush();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            file = new MockMultipartFile(file.getName(), file.getOriginalFilename(), file.getContentType(), bais);

            baos.close();
            bais.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
