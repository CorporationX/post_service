package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 clientAmazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        byte[] imageData = null;
        try {
            // Проверяем размер файла перед сжатием
            if (file.getSize() > 5 * 1024 * 1024) {
                // Сжимаем файл перед загрузкой
                imageData = compressImageSize(file);
            } else {
                // Используем оригинальный файл без сжатия
                imageData = file.getBytes();
            }
        } catch (IOException e) {
            log.error("Error compressing image: ", e.getMessage());
            throw new RuntimeException("Error compressing image: " + e.getMessage());
        }

        long fileSize = imageData.length;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, new ByteArrayInputStream(imageData), objectMetadata);
            clientAmazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("Error uploading file " + e.getMessage());
            throw new DataValidationException("Error uploading file " + e.getMessage());
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

    public byte[] compressImageSize(MultipartFile file){
        try {
            // Загрузить изображение в объект BufferedImage
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            // Начальное значение качества сжатия
            float quality = 1.0f;
            // Устанавливаем максимальный размер файла в байтах (5 Мб)
            long maxSizeBytes = 5 * 1024 * 1024;

            // Применить сжатие и изменить размер
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while (true) {
                Thumbnails.of(originalImage)
                        .size(1080, 1080)
                        .outputFormat("jpg")
                        .outputQuality(quality) // Используем текущее значение качества
                        .toOutputStream(outputStream);
                if (outputStream.size() <= maxSizeBytes || quality <= 0.1) { // Учтем минимальное качество
                    break;
                }
                quality -= 0.1f; // Уменьшаем качество на 0.1
                outputStream.reset(); // Очищаем поток для нового сжатия
            }
            return outputStream.toByteArray();
        } catch (IOException e) {

            throw new RuntimeException("Error compressing image: " + e.getMessage(), e);
        }
    }


}
