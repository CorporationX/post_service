package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.exception.ImageProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Сервис для работы с S3-хранилищем.
 * Предоставляет методы для загрузки изображений и проверки их существования.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName.name}")
    private String bucketName;

    /**
     * Загружает изображение в S3-хранилище.
     *
     * @param key         Ключ для хранения изображения в S3
     * @param image       Изображение для загрузки
     * @param contentType Тип содержимого изображения
     * @throws IOException Если произошла ошибка при записи изображения
     */
    public void uploadToS3(String key, BufferedImage image, String contentType) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, contentType.split("/")[1], os);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(os.size());
            metadata.setContentType(contentType);

            try (ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray())) {
                amazonS3.putObject(bucketName, key, is, metadata);
            }
        }
    }

    /**
     * Удаляет файл из S3-хранилища.
     *
     * @param fileKey ключ файла для удаления
     */
    public void deleteFile(String fileKey) {
        if (fileKey == null) {
            return;
        }

        try {
            amazonS3.deleteObject(bucketName, fileKey);
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                log.debug("File {} not found in S3, skipping deletion", fileKey);
                return;
            }
            log.error("Failed to delete file {}: {}", fileKey, e.getMessage());
            throw new ImageProcessingException("Failed to delete file from S3");
        }
    }
}