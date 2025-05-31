package faang.school.postservice.service.s3;

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
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


import java.time.LocalDateTime;
import java.util.UUID;
import java.io.InputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket-name}")
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
            throw new RuntimeException("Failed to upload file", e);
        }

        return Resource.builder()
                .key(mediaKey)
                .size(fileSize)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .createdAt(LocalDateTime.now())
                .build();
    }
}