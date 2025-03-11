package faang.school.postservice.utils;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String uploadFile(byte[] byteArray, String contentType, String bucketName) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error("Minio create bucket error", e);
            throw new RuntimeException("Minio create bucket error");
        }

        String fileId = UUID.randomUUID().toString() + LocalDateTime.now();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileId)
                            .stream(inputStream, byteArray.length, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            log.error("Minio put object error", e);
            throw new RuntimeException("Minio put object error");
        }

        return fileId;
    }

    public byte[] getFile(String bucketName, String fileId) {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileId)
                        .build()
        )) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toByteArray();
        } catch (Exception e) {
            log.error("Minio get object error", e);
            throw new RuntimeException("Minio get object error");
        }
    }

    public void completeRemoval(String key) {
        log.info("Удаление объекта из MinIO: bucket={}, key={}", bucketName, key);
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Ключ файла (key) не может быть пустым");
        }
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .build()
            );
            log.info("Файл {} успешно удалён из MinIO", key);
        } catch (Exception e) {
            log.error("Ошибка при удалении файла {} из MinIO: {}", key, e.getMessage());
            throw new RuntimeException("Ошибка при удалении файла из MinIO", e);
        }
    }
}
