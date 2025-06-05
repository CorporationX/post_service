package faang.school.postservice.service.s3;

import faang.school.postservice.exception.FileDownloadFailedException;
import faang.school.postservice.exception.FileUploadFailedException;
import faang.school.postservice.model.Resource;
import io.awspring.cloud.s3.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;


import java.time.LocalDateTime;
import java.util.UUID;
import java.io.InputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket-name}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String mediaKey = String.format("%s/%s.%s", folder, UUID.randomUUID(), extension);
        
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(mediaKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, fileSize));
            log.info("File uploaded successfully to S3: {}", mediaKey);
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new FileUploadFailedException("Failed to upload file");
        }

        return Resource.builder()
                .key(mediaKey)
                .size(fileSize)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public InputStream getFileAsInputStream(Resource resource) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(resource.getKey())
                    .build();
            return s3Client.getObject(getObjectRequest);
        } catch (Exception e) {
            log.error("Failed to download file from S3: {}", resource.getKey(), e);
            throw new FileDownloadFailedException("Failed to download file");
        }
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
        log.info("File deleted successfully from S3: {}", key);
    }
}