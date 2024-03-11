package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3 clientAmazonS3;
   private final int TARGET_WIDTH = 1080;
   private final int TARGET_HEIGHT = 566;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        byte[] imageData = compressImage(file);
        long fileSize = imageData.length;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());

        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, new ByteArrayInputStream(imageData), objectMetadata);
            clientAmazonS3.putObject(putObjectRequest);
        } catch (RuntimeException e) {
            log.error("Error uploading file " + e.getMessage());
            throw new RuntimeException("Error uploading file " + e.getMessage());
        }
        Resource resource = Resource.builder()
                .key(key)
                .size(fileSize)
                .createdAt(LocalDateTime.now())
                .type(file.getContentType())
                .name(file.getName())
                .build();
        log.info("Created a new resource");
        return resource;
    }

    public void deleteFile(String key) {
        clientAmazonS3.deleteObject(bucketName, key);
        log.info("Resource deleted: {}", key);
    }

    private byte[] compressImage(MultipartFile file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            if (originalWidth <= TARGET_WIDTH && originalHeight <= TARGET_HEIGHT) {
                return file.getBytes();
            }

            int newWidth, newHeight;
            double aspectRatio = (double) originalWidth / originalHeight;
            if (originalWidth >= originalHeight) {
                newWidth = TARGET_WIDTH;
                newHeight = (int) (newWidth / aspectRatio);
            } else {
                newHeight = TARGET_HEIGHT;
                newWidth = (int) (newHeight * aspectRatio);
            }

            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error compressing image: " + e.getMessage(), e);
            throw new RuntimeException("Error compressing image: " + e.getMessage(), e);
        }
    }
}
