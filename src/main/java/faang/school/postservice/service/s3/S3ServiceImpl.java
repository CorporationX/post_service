package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.dto.resource.ResourceObjectResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Setter
    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public void uploadFile(byte[] fileContent, @NonNull String contentType, @NonNull String fileKey) {
        if (fileContent.length == 0) {
            throw new IllegalArgumentException("File content is empty with key %s".formatted(fileKey));
        }
        ObjectMetadata objectMetadata = getObjectMetadata(contentType, fileContent.length);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey,
                new ByteArrayInputStream(fileContent),
                objectMetadata);
        s3Client.putObject(putObjectRequest);
        log.debug("File with key: {} uploaded to s3", fileKey);
    }

    @Override
    public void deleteFile(@NonNull String fileKey) {
        s3Client.deleteObject(bucketName, fileKey);
        log.info("File with key {} deleted", fileKey);
    }

    @Override
    public ResourceObjectResponse downloadFile(@NonNull String fileKey) {
        S3Object s3Object = s3Client.getObject(bucketName, fileKey);
        log.info("Download file with key {} from s3", fileKey);
        return ResourceObjectResponse.builder()
                .content(s3Object.getObjectContent())
                .contentType(s3Object.getObjectMetadata().getContentType())
                .contentLength(s3Object.getObjectMetadata().getContentLength())
                .build();
    }

    private ObjectMetadata getObjectMetadata(String contentType, long contentLength) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.setContentLength(contentLength);
        return objectMetadata;
    }
}
