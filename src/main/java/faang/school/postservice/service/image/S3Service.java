package faang.school.postservice.service.image;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
public class S3Service {

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
        log.info("S3Client initialized for: {}", endpoint);
    }

    public void uploadFile(String objectKey, byte[] fileBytes, String contentType) {
        log.info("Uploading to MinIO: {}", objectKey);
        s3Client.putObject(request ->
                        request
                                .bucket(bucketName)
                                .key(objectKey)
                                .contentType(contentType),
                RequestBody.fromBytes(fileBytes)
        );
        log.info("Successfully uploaded to MinIO: {}", objectKey);
    }

    public byte[] downloadFile(String objectKey) {
        log.info("Downloading from MinIO: {}", objectKey);
        return s3Client.getObjectAsBytes(request ->
                        request
                                .bucket(bucketName)
                                .key(objectKey))
                .asByteArray();
    }

    public void deleteFile(String objectKey) {
        log.info("Deleting from MinIO: {}", objectKey);
        s3Client.deleteObject(request ->
                request
                        .bucket(bucketName)
                        .key(objectKey));
        log.info("Successfully deleted from MinIO: {}", objectKey);
    }

    public void deleteFiles(List<String> objectKeys) {
        if (objectKeys == null || objectKeys.isEmpty()) {
            return;
        }

        List<ObjectIdentifier> objectsToDelete = objectKeys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        s3Client.deleteObjects(request ->
                request.bucket(bucketName)
                        .delete(deleteRequest ->
                                deleteRequest
                                        .objects(objectsToDelete)));
        log.info("Successfully deleted {} files from MinIO", objectKeys.size());
    }
}