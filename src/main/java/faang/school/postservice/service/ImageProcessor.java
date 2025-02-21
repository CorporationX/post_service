package faang.school.postservice.service;

import faang.school.postservice.exception.FileFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class ImageProcessor {

    public BufferedImage resizeImage(MultipartFile file, int maxSize) throws IOException {
        InputStream inputStream = file.getInputStream();
        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new FileFormatException("Invalid file format");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        int newWidth = originalWidth;
        int newHeight = originalHeight;

        if (originalWidth > maxSize || originalHeight > maxSize) {
            if (originalWidth > originalHeight) {
                newWidth = maxSize;
                newHeight = (int) ((double) maxSize / originalWidth * originalHeight);
            } else {
                newHeight = maxSize;
                newWidth = (int) ((double) maxSize / originalHeight * originalWidth);
            }
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    public byte[] bufferedImageToByteArray(BufferedImage image, String format) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            return baos.toByteArray();
        }
    }
}
