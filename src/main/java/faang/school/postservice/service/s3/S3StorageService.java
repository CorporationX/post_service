package faang.school.postservice.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import jakarta.annotation.PostConstruct;
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
public class S3StorageService {

    @Value("${services.s3.endpoint}")
    private String endPoint;

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private AmazonS3 s3Client;

    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                endPoint, "us-east-1"))
                .withPathStyleAccessEnabled(true)
                .build();
        try {
            if (!s3Client.doesBucketExistV2(bucketName)) {
                s3Client.createBucket(bucketName);
                log.info("Bucket created: {}", bucketName);
            }
        } catch (AmazonServiceException e) {
            log.error("Error checking/creating bucket {}: {}", bucketName, e.getMessage());
        }
    }

    public void uploadFile(String key, InputStream inputStream, long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);

        try {
            s3Client.putObject(bucketName, key, inputStream, metadata);
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
            S3Object s3Object = s3Client.getObject(bucketName, key);
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
            s3Client.deleteObject(bucketName, key);
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
        return s3Client.generatePresignedUrl(req).toString();
    }
}
