package faang.school.postservice.service.postImage;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageResizer {

    /**
     * Обрабатывает изображение по условию:
     * - Если ширина > 1080 или высота > 566 для горизонтальных изображений,
     *   или ширина = высота и > 1080 для квадратных,
     *   то уменьшает изображение до заданных размеров.
     *
     * @param inputFile  исходный файл изображения
     * @param outputFile файл для сохранения обработанного изображения
     * @throws IOException при ошибках чтения/записи
     */

    public void processImage(File inputFile, File outputFile) throws IOException {
        BufferedImage originalImage = javax.imageio.ImageIO.read(inputFile);
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        boolean isHorizontal = width > height;
        boolean isSquare = width == height;

        int targetWidth = width;
        int targetHeight = height;

        if (isHorizontal) {
            if (width > 1080 || height > 566) {
                targetWidth = Math.min(width, 1080);
                targetHeight = Math.min(height, 566);
            } else {
                targetWidth = width;
                targetHeight = height;
            }
        } else if (isSquare) {
            if (width > 1080) {
                targetWidth = 1080;
                targetHeight = 1080;
            } else {
                targetWidth = width;
                targetHeight = height;
            }
        } else {
            targetWidth = width;
            targetHeight = height;
        }
        if (targetWidth == width && targetHeight == height) {
            javax.imageio.ImageIO.write(originalImage, "jpg", outputFile);
            return;
        }
        Thumbnails.of(inputFile)
                .size(targetWidth, targetHeight)
                .keepAspectRatio(true)
                .toFile(outputFile);
    }
}
