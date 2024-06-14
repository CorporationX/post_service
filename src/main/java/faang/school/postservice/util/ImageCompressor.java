package faang.school.postservice.util;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class ImageCompressor {

    private static final int MAX_WIDTH_LANDSCAPE = 1080;
    private static final int MAX_HEIGHT_LANDSCAPE = 566;
    private static final int MAX_SIZE_SQUARE = 1080;

    public MultipartFile compressImage(BufferedImage image, MultipartFile file) {
        log.info("start compress {} image", file.getName());
        int width = image.getWidth();
        int height = image.getHeight();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais = null;
        MultipartFile resizedFile = null;

        try {
            if (width > MAX_WIDTH_LANDSCAPE && height <= MAX_HEIGHT_LANDSCAPE) {
                // Горизонтальное изображение
                Thumbnails.of(image)
                        .size(MAX_WIDTH_LANDSCAPE, (MAX_WIDTH_LANDSCAPE * height) / width)
                        .toOutputStream(baos);
            } else if (width > MAX_SIZE_SQUARE || height > MAX_SIZE_SQUARE) {
                // Квадратное или любое изображение больше чем 1080x1080
                int newWidth = MAX_SIZE_SQUARE;
                int newHeight = (MAX_SIZE_SQUARE * height) / width;

                if (newHeight > MAX_SIZE_SQUARE) {
                    newHeight = MAX_SIZE_SQUARE;
                    newWidth = (MAX_SIZE_SQUARE * width) / height;
                }

                Thumbnails.of(image)
                        .size(newWidth, newHeight)
                        .toOutputStream(baos);
            }

            bais = new ByteArrayInputStream(baos.toByteArray());
            resizedFile = new MockMultipartFile(file.getName(), file.getOriginalFilename(), file.getContentType(), bais);

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Image compression error, file {}, {}", file.getName(), file.getContentType());
            throw new RuntimeException("Image compression error");
        } finally {
            try {
                baos.close();
                if (bais != null) {
                    bais.close();
                }
            } catch (IOException e) {
                log.error("Input/Output stream closing error");
                e.printStackTrace();
            }
        }

        return resizedFile;
    }
}
