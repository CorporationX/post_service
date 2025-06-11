package faang.school.postservice.service.s3;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3StorageService {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void init() {
        try {
            if (!amazonS3.doesBucketExistV2(bucketName)) {
                amazonS3.createBucket(bucketName);
                log.info("Bucket created: {}", bucketName);
            } else {
                log.info("Bucket {} already exists", bucketName);
            }
        } catch (AmazonServiceException e) {
            log.error("Error checking/creating bucket {}: {}", bucketName, e.getMessage());
            throw e;
        }
    }

    public void uploadFile(String key, InputStream inputStream, long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);

        try {
            amazonS3.putObject(bucketName, key, inputStream, metadata);
            log.info("File successfully uploaded to S3: {}", key);
        } catch (AmazonServiceException e) {
            log.error("Error uploading file {}: {}", key, e.getMessage());
            throw e;
        }
    }

    public void uploadFile(String key, MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            uploadFile(key, is, file.getSize(), file.getContentType());
        }
    }

    public Optional<byte[]> downloadFile(String key) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, key);
            try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
                return Optional.of(inputStream.readAllBytes());
            }
        } catch (AmazonServiceException | IOException e) {
            log.error("Error downloading file {}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    public void deleteFile(String key) {
        try {
            amazonS3.deleteObject(bucketName, key);
            log.info("File successfully deleted {}", key);
        } catch (AmazonServiceException e) {
            log.error("Error occurred while deleting file {}: {}", key, e.getMessage());
        }
    }

    public String generatePresignedUrl(String key) {
        Date expiration = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        return amazonS3.generatePresignedUrl(req).toString();
    }
}
