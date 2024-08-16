package faang.school.postservice.service.s3;

import faang.school.postservice.model.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinioS3Client {
//    @Value("${services.s3.bucketName}")
//    private String bucketName;
//
//    @Value("${services.s3.accessKey}")
//    private String accessKey;
//
//    @Value("${services.s3.secretKey}")
//    private String secretKey;
//
//    @Value("${services.s3.endpoint}")
//    private String endpoint;
    private final String bucketName = "corpbucket";

    private final String accessKey = "user";

    private final String secretKey = "password";

    private final String endpoint = "http://localhost:9000";

    private final S3Client s3Client;

    public MinioS3Client() {
        s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_WEST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());

        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(file.getBytes());
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromByteBuffer(byteBuffer));
        } catch (S3Exception | IOException e) {
            System.err.println("Failed to upload file: " + e.getMessage());
        }

        return Resource.builder()
                .key(key)
                .type(file.getContentType())
                .name(file.getOriginalFilename())
                .size(fileSize)
                .build();
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

    public void createBucket(String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            System.out.println("Bucket created successfully: " + bucketName);
        } catch (S3Exception e) {
            System.err.println("Failed to create bucket: " + e.awsErrorDetails().errorMessage());
        }
    }

    public List<String> listBuckets() {
        List<String> buckets = new ArrayList<>();
        try {
            ListBucketsResponse bucketsResponse = s3Client.listBuckets();
            for (Bucket bucket : bucketsResponse.buckets()) {
                buckets.add("Bucket Name: " + bucket.name());
            }
        } catch (S3Exception e) {
            System.err.println("Failed to list buckets: " + e.awsErrorDetails().errorMessage());
        }
        return buckets;
    }
}
