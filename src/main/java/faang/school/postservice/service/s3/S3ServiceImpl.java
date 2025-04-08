package faang.school.postservice.service.s3;

import faang.school.postservice.dto.resource.S3UploadDto;
import faang.school.postservice.exception.S3Exception;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadResource(S3UploadDto s3UploadDto) {
        long fileSize = s3UploadDto.fileSize();
        String fileName = s3UploadDto.fileName();
        String contentType = s3UploadDto.fileType();
        String key = s3UploadDto.postId().toString() + "/" + System.currentTimeMillis() + fileName;
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(contentType)
                .key(key)
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(s3UploadDto.bytes()));
            log.debug("Uploaded file: {} to bucket: {}", key, bucketName);
        } catch (Exception e) {
            log.error("Failed to upload file: {} from bucket: {}", e.getMessage(), bucketName);
            throw new S3Exception(e);
        }

        return Resource.builder()
                .key(key)
                .size(fileSize)
                .createdAt(LocalDateTime.now())
                .name(fileName)
                .type(contentType)
                .build();
    }

    @Override
    public InputStream downloadResource(String key) {
        log.debug("Downloading file for key: {} from bucket: {}", key, bucketName);
        try {
            return s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build());
        } catch (Exception e) {
            log.error("Failed to download file for key: {} from bucket: {}", key, bucketName);
            throw new S3Exception(e);
        }
    }

    @Override
    public void deleteResource(String key) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()
            );
            log.debug("Resource with key: {} deleted from bucket: {}", key, bucketName);
        } catch (Exception e) {
            log.error("Failed to delete resource with key: {} from bucket: {}", key, bucketName);
            throw new S3Exception(e);
        }
    }
}
