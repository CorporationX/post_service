package faang.school.postservice.service.postImage;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * S3Service — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author Пользователь
 * @since 10.08.2025
 */
@Slf4j
@Service
public class S3Service {

    private final ImageResizer imageResizer;
    private final MinioClient minioClient;
    private final String bucketName;

    public S3Service(
            ImageResizer imageResizer, @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket-name}") String bucketName, PostRepository postRepository
    ) {
        this.imageResizer = imageResizer;
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;
    }

    public Resource addFile(Long postId, @NotNull MultipartFile file) throws Exception {
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Файл слишком большой (макс. 5 МБ)");
        }

        String originalFilename = file.getOriginalFilename();
        String key;

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Можно загружать только изображения");
        }

        File tempFile = File.createTempFile("upload_", "_" + originalFilename);
        try {
            file.transferTo(tempFile);

            File processedFile = File.createTempFile("processed_", "_" + originalFilename);
            imageResizer.processImage(tempFile, processedFile);

            long fileSize = processedFile.length();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileSize);
            metadata.setContentType(contentType);

            key = "posts:" + originalFilename;

            try (InputStream inputStream = new FileInputStream(processedFile)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(key)
                                .stream(inputStream, fileSize, -1)
                                .contentType(contentType)
                                .build()
                );
            }
            tempFile.delete();
            processedFile.delete();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception("ошибка создания файла");
        }
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(file.getSize());
        resource.setName(originalFilename);
        resource.setType(contentType);
        resource.setCreatedAt(LocalDateTime.now());
        return resource;
    }

    public List<Resource> uploadsFiles(Long postId, @NotNull List<MultipartFile> files) throws Exception {
        if (files.size() > 10) {
            throw new IllegalArgumentException("Можно загружать не более 10 файлов");
        }
        try {
            List<Resource> resources = new ArrayList<>();
            for (MultipartFile file : files) {
                Resource resource = addFile(postId, file);
                resources.add(resource);
            }
            return resources;
        } catch (FileNotFoundException e) {
            log.error("Файл не найден" + e.getMessage());
        }
        return null;
    }

    public ResponseEntity<InputStreamResource> getFile(Long postId, @NotNull String key) {
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .build()
            );

            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .build()
            );
            String contentType = "application/octet-stream";
            if (key.endsWith(".png")) {
                contentType = "image/png";
            } else if (key.endsWith(".jpg") || key.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (key.endsWith(".gif")) {
                contentType = "image/gif";
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", key);
            headers.setContentType(MediaType.parseMediaType(contentType));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(stat.size())
                    .body(new InputStreamResource(stream));

        } catch (Exception e) {
            log.error("Error getting file from MinIO: {}", e.getMessage());
            throw new RuntimeException("Failed to get file from storage", e);
        }
    }

    public void deleteFile(Long postId, List<String> keys) {

        try {
            for (String key : keys) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(key)
                                .build()
                );
            }
        }
        catch (Exception e) {
            log.error("Error removing file from MinIO: {}", e.getMessage());
        }
    }
}