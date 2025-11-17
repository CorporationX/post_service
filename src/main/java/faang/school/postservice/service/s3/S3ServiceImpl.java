package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.exception.S3OperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 amazonS3;

    @Value("${s3.bucketName}")
    private String bucketName;

    private final AtomicBoolean bucketChecked = new AtomicBoolean(false);

    @Override
    @Retryable(value = {Exception.class}, backoff = @Backoff(delay = 1000))
    public String uploadFile(MultipartFile file) {
        try {
            if (bucketChecked.compareAndSet(false, true)) {
                createBucketIfNotExists();
            }

            String key = generateKey(file.getOriginalFilename());
            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentLength(file.getSize());
            metaData.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucketName, key, inputStream, metaData));
            }

            log.info("File uploaded successfully: {}", key);
            return key;

        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            throw new S3OperationException("Failed to upload file: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    @Retryable(value = {Exception.class}, backoff = @Backoff(delay = 1000))
    public void deleteFile(String key) {
        try {
            amazonS3.deleteObject(bucketName, key);
            log.info("File deleted successfully: {}", key);
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", key, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    private void createBucketIfNotExists() {
        try {
            if (!amazonS3.doesBucketExistV2(bucketName)) {
                amazonS3.createBucket(bucketName);
                log.info("Bucket created: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Error checking/creating bucket", e);
            throw new RuntimeException("Bucket operation failed", e);
        }
    }

    public String generateKey(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }
}
