package faang.school.postservice.service.minio;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.config.s3.MinioConfig;
import faang.school.postservice.exceptions.FileException;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class MinioServiceImpl implements MinioService {
    private final MinioConfig minioConfig;

    @Value("${MINIO_BUCKET_NAME}")
    private String bucketName;
    @Value("${services.s3.posts.download.times-live-url-minutes}")
    private int MAX_LIVE_URL_MINUTES;


    @Override
    public Resource uploadImage(byte[] imageData, String folder, String originalFileName, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageData.length);
            metadata.setContentType(contentType);

            String timestamp = String.valueOf(System.currentTimeMillis());
            String random = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999));
            String key = String.format("%s/%s_%s_%s", folder, timestamp, random, originalFileName);

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    new ByteArrayInputStream(imageData),
                    metadata
            );

            minioConfig.s3Client().putObject(putObjectRequest);

            log.info("Image uploaded successfully. Key: {}, Size: {} bytes", key, imageData.length);

            return Resource.builder()
                    .key(key)
                    .size(imageData.length)
                    .type(contentType)
                    .name(originalFileName)
                    .build();

        } catch (Exception e) {
            log.error("Error uploading image");
            throw new FileException("Error uploading image");
        }
    }

    @Override
    public List<String> downloadImage(String key, String folder) {
        try {
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(MAX_LIVE_URL_MINUTES);
            expiration.setTime(expTimeMillis);
            String normalizedFolder = folder.endsWith("/") ? folder : folder + "/";

            ListObjectsV2Request request = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(folder);

            ListObjectsV2Result result = minioConfig.s3Client().listObjectsV2(request);
            log.info("Found {} objects in folder: {}", result.getObjectSummaries().size(), folder);
            return result.getObjectSummaries().stream()
                    .filter(object -> !object.getKey().equals(normalizedFolder))
                    .map(object -> generateTemporaryUrl(object.getKey()))
                    .toList();

        } catch (AmazonS3Exception e) {
            log.error("Error getting files from folder: {}", folder, e);
            throw new FileException("Error getting files from folder");
        }
    }

    @Override
    public void deleteImage(String key) {
        try {
            minioConfig.s3Client().deleteObject(bucketName, key);
        } catch (AmazonS3Exception e) {
            throw new FileException("Error when deleting image");
        }
    }

    private String generateTemporaryUrl(String key) {
        try {
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(MAX_LIVE_URL_MINUTES);
            expiration.setTime(expTimeMillis);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, key)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);

            return minioConfig.s3Client().generatePresignedUrl(generatePresignedUrlRequest).toString();

        } catch (AmazonS3Exception e) {
            log.error("Error generating temporary URL for key: {}", key, e);
            throw new FileException("Error generating download URL");
        }
    }
}