package faang.school.postservice.service.s3;

import faang.school.postservice.exception.FileUploadException;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioS3Client {
    private final S3Client s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        String key = generateFileKey(folder, file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(inputStream, fileSize));
        } catch (S3Exception | IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            throw new FileUploadException("Failed to upload file to S3", e);
        }

        return Resource.builder()
                .key(key)
                .type(file.getContentType())
                .name(file.getOriginalFilename())
                .size(fileSize)
                .build();
    }

    private String generateFileKey(String folder, String originalFilename) {
        return String.format("%s/%d%s", folder, System.currentTimeMillis(), originalFilename);
    }

    public InputStream downloadFile(String key) {
        return s3Client.getObject(GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    public void deleteFIle(String fileKey, String smallFileKey) {
        s3Client.deleteObject(DeleteObjectRequest
                .builder()
                .bucket(bucketName)
                .key(fileKey)
                .build());

        s3Client.deleteObject(DeleteObjectRequest
                .builder()
                .bucket(bucketName)
                .key(smallFileKey)
                .build());
    }
}
