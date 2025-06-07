package faang.school.postservice.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.FileProcessException;
import faang.school.postservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Retryable(retryFor = {IOException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    @Override
    public String uploadFile(ByteArrayInputStream file, String path) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/jpeg");
        objectMetadata.setContentLength(file.available());
        String key = String.format("%s resized_image.jpg", path);
        PutObjectRequest savedFile = new PutObjectRequest(bucketName, key, file, objectMetadata);
        amazonS3.putObject(savedFile);

        return key;
    }

    @Override
    public void deleteFile(String fileKey) {
        amazonS3.deleteObject(bucketName, fileKey);
    }

    @Override
    public InputStream downloadFile(String fileKey) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, fileKey);
            return s3Object.getObjectContent();
        } catch (SdkClientException e) {
            log.error("Exception was thrown while downloading file", e);
            throw new FileProcessException("Error while downloading file with key %s from bucket %s"
                    .formatted(fileKey, bucketName));
        }
    }
}
