package faang.school.postservice.service.image;

import faang.school.postservice.config.resource.ResourceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@RequiredArgsConstructor
@Slf4j
@Component
public class ImageProcessor {
    private final ResourceProperties resourceProperties;

    public MultipartFile compressImage(MultipartFile originalFile) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());

            if (originalImage == null) {
                log.warn("Cannot read image file, returning original: {}", originalFile.getOriginalFilename());
                return originalFile;
            }

            BufferedImage compressedImage = resizeImage(originalImage);
            String formatName = getFormatName(originalFile.getContentType());
            ImageIO.write(compressedImage, formatName, baos);

            return new CustomMultipartFile(
                    originalFile.getOriginalFilename(),
                    originalFile.getContentType(),
                    baos.toByteArray()
            );
        } catch (Exception e) {
            log.error("Failed to compress image: {}", originalFile.getOriginalFilename(), e);
            return originalFile;
        }
    }

    public boolean needsCompression(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return false;
            }
            int width = image.getWidth();
            int height = image.getHeight();

            return width > resourceProperties.getImageMaxWidth() ||
                    height > resourceProperties.getImageMaxHeight();
        } catch (Exception e) {
            log.warn("Cannot determine if image needs compression: {}", file.getOriginalFilename(), e);
            return false;
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        int maxWidth = resourceProperties.getImageMaxWidth();
        int maxHeight = resourceProperties.getImageMaxHeight();

        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int targetWidth = (int) (originalWidth * ratio);
        int targetHeight = (int) (originalHeight * ratio);

        targetWidth = targetWidth % 2 == 0 ? targetWidth : targetWidth - 1;
        targetHeight = targetHeight % 2 == 0 ? targetHeight : targetHeight - 1;

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, targetWidth, targetHeight);
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        return resizedImage;
    }

    public String getFormatName(String contentType) {
        if (contentType == null) return "jpg";

        return switch (contentType) {
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> "jpg";
        };
    }
}
