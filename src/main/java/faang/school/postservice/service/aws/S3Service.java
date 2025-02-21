package faang.school.postservice.service.aws;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3AsyncClient s3AsyncClient;
    private final S3Presigner s3Presigner;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    public CompletableFuture<PutObjectResponse> uploadFileAsync(String bucketName,
                                                                String key,
                                                                Map<String, String> metadata,
                                                                byte[] fileBytes) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .metadata(metadata)
                .build();

        CompletableFuture<PutObjectResponse> response =
                s3AsyncClient.putObject(objectRequest, AsyncRequestBody.fromBytes(fileBytes));

        return response.thenApply(resp -> {
            logger.info("File uploaded successfully: {}", key);;
            return resp;
        }).exceptionally(ex -> {
            logger.error("Failed to upload file: {}", key, ex);
            throw new RuntimeException("Failed to upload file", ex);
        });
    }

    public CompletableFuture<Void> deleteFileAsync(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3AsyncClient.deleteObject(deleteObjectRequest)
                .thenAccept(response -> logger.info("File deleted successfully: {}", key))
                .exceptionally(ex -> {
                    logger.error("Failed to delete file: {}", key, ex);
                    throw new RuntimeException("Failed to delete file", ex);
                });
    }

    public String createPresignedGetUrl(String bucketName, String keyName) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                .getObjectRequest(objectRequest)

                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        logger.info("Presigned URL: [{}]", presignedRequest.url().toString());
        logger.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

        return presignedRequest.url().toExternalForm();
    }

    public byte[] getObjectBytes(String bucketName, String keyName) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            CompletableFuture<ResponseBytes<GetObjectResponse>> future =
                    s3AsyncClient.getObject(objectRequest, AsyncResponseTransformer.toBytes());

            ResponseBytes<GetObjectResponse> responseBytes = future.join();

            return responseBytes.asByteArray();
        } catch (
                S3Exception e) {
            logger.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return new byte[0];
    }
}