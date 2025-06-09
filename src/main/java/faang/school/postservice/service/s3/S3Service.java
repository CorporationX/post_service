package faang.school.postservice.service.s3;

import faang.school.postservice.config.s3.S3Properties;
import faang.school.postservice.exception.file.FileDownloadException;
import faang.school.postservice.exception.file.FileNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public InputStream download(String fileKey) {
        String bucketName = s3Properties.getBucket();

        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            return s3Client.getObject(request);
        } catch (NoSuchKeyException e) {
            log.warn("Файл не найден: {}", fileKey);
            throw new FileNotFoundException("Файл не найден: " + fileKey);
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла {}: {}", fileKey, e.getMessage());
            throw new FileDownloadException("Не удалось скачать файл: " + fileKey);
        }
    }

    public void delete(String fileKey) {
        String bucketName = s3Properties.getBucket();

        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(request);
            log.info("Файл успешно удалён: {}", fileKey);
        } catch (NoSuchKeyException e) {
            log.warn("Файл не найден для удаления: {}", fileKey);
            throw new FileNotFoundException("Файл не найден: " + fileKey);
        } catch (Exception e) {
            log.error("Ошибка при удалении файла {}: {}", fileKey, e.getMessage());
            throw new IllegalStateException("Не удалось удалить файл: " + fileKey, e);
        }
    }

    public void upload(byte[] bytes, String key, String contentType) {
        String bucketName = s3Properties.getBucket();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(bytes)
        );
    }
}
