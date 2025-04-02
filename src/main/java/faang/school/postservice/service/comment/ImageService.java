package faang.school.postservice.service.comment;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.exception.ImageProcessingException;
import faang.school.postservice.exception.ImageReadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final AmazonS3 amazonS3;
    private final String bucketName = "corpbucket";

    public ImageKeys uploadResizedImages(MultipartFile file, long id) {
        validateImage(file);

        String largeKey = String.format("comments/%d_large.jpg", id);
        String smallKey = String.format("comments/%d_small.jpg", id);

        byte[] originalBytes = null;

        try {
            originalBytes = file.getBytes();
        } catch (IOException e) {
            log.error(ErrorMessages.FAILED_READ.getMessage(), e);
            throw new ImageReadException(ErrorMessages.FAILED_READ.getMessage(), e);
        }

        byte[] largeBytes = resize(originalBytes, 1080);
        byte[] smallBytes = resize(originalBytes, 170);

        upload(largeKey, largeBytes);
        upload(smallKey, smallBytes);

        return new ImageKeys(largeKey, smallKey);
    }

    public record ImageKeys(String largeKey, String smallKey) {
    }

    public void deleteImageIfExists(String fileKey) {
        if (fileKey != null && !fileKey.isBlank()) {
            amazonS3.deleteObject(bucketName, fileKey);
        }
    }

    private void upload(String key, byte[] bytes) {
        InputStream is = new java.io.ByteArrayInputStream(bytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType("image/jpeg");

        amazonS3.putObject(bucketName, key, is, metadata);
    }

    private byte[] resize(byte[] bytes, int maxSize) {
        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(bytes));

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            if (Math.max(width, height) <= maxSize) {
                return bytes;
            }

            float scale = (float) maxSize / Math.max(width, height);
            int newWidth = Math.round(width * scale);
            int newHeight = Math.round(height * scale);

            Image scaledInstance = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(scaledInstance, 0, 0, null);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error(ErrorMessages.FAILED_RESIZE.getMessage());
            throw new ImageProcessingException(ErrorMessages.FAILED_RESIZE.getMessage());
        }
    }


    private void validateImage(MultipartFile file) {
        if (file.getSize() > 5 * 1024 * 1024) {
            log.error(ErrorMessages.IMAGE_SIZE_EXCEED.getMessage());
            throw new ImageProcessingException(ErrorMessages.IMAGE_SIZE_EXCEED.getMessage());
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.error(ErrorMessages.INVALID_TYPE.getMessage());
            throw new ImageProcessingException(ErrorMessages.INVALID_TYPE.getMessage());
        }

        List<String> allowedTypes = List.of("image/jpeg", "image/png");
        if (!allowedTypes.contains(contentType)) {
            log.error(ErrorMessages.INVALID_FORMAT.getMessage());
            throw new ImageProcessingException(ErrorMessages.INVALID_FORMAT.getMessage());
        }
    }
}
