package faang.school.postservice.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsService {
    private final S3Client s3;

    public void uploadFile(String bucketName, String keyName, byte[] fileBytes) {
            RequestBody requestBody = RequestBody.fromBytes(fileBytes);

            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            s3.putObject(putOb, requestBody);
            log.info("File uploaded to bucket({}): {}", bucketName, keyName);
    }

    public byte[] downloadFile(String bucketName, String keyName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        try (InputStream inputStream = s3.getObject(getObjectRequest)) {
            log.info("File {} was downloaded from bucket: ({})", keyName, bucketName);
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Error uploading file with key: ({}) to S3", keyName);
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String bucketName, String keyName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build();
        s3.deleteObject(request);
        log.info("File deleted from bucket({}): {}", bucketName, keyName);
    }
}
