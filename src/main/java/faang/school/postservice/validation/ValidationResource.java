package faang.school.postservice.validation;

import faang.school.postservice.exception.DataValidationException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ValidationResource {

    private static final int WIDTH = 1080;
    private static final int HEIGHT = 566;
    private static final int MAX_SIZE_SQUARE = 1080;
    private static final long MAX_SIZE_FILE =  5 * 1024;
    //добавить проверку на количество картинок к одному посту

    public static BufferedImage correctedBuild(MultipartFile image) throws IOException {
        BufferedImage originalImage = ImageIO.read(image.getInputStream());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        boolean isHorizontal = width > height;

        int targetWidth = width;
        int targetHeight = height;
        checkPictureWeight(image);

        if (isHorizontal) {
            if (width > WIDTH || height > HEIGHT) {
                double newWidth = (double) WIDTH / width;
                double newHeight = (double) HEIGHT / height;
                double scaleFactor = Math.min(newWidth, newHeight);
                targetWidth = (int) (width * scaleFactor);
                targetHeight = (int) (height * scaleFactor);
            }
        } else {
            if (width > MAX_SIZE_SQUARE || height > MAX_SIZE_SQUARE) {
                double newWidth = (double) MAX_SIZE_SQUARE / width;
                double newHeight = (double) MAX_SIZE_SQUARE / height;
                double scaleFactor = Math.min(newWidth, newHeight);
                targetWidth = (int) (width * scaleFactor);
                targetHeight = (int) (height * scaleFactor);
            }
        }

        BufferedImage updateImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = updateImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, WIDTH, HEIGHT, null);
        graphics.dispose();

        return updateImage;
    }

    private static void checkPictureWeight(MultipartFile file) {
        double sizeFile = (double) file.getSize() / 1024;
        if (sizeFile > MAX_SIZE_FILE) {
            throw new DataValidationException("File exceeds allowed size " + MAX_SIZE_FILE);
        }
    }
}
