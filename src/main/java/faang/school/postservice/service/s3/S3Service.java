package faang.school.postservice.service.s3;

import faang.school.postservice.validation.ValidationResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class S3Service {
    private final S3Client s3Client;
    @Value("${services.s3.bucket}")
    private String bucketName;

    public String generateKeyForImage(MultipartFile image) {
        String key = String.format("%s%s", System.currentTimeMillis(), image.getOriginalFilename());

        try (InputStream inputStream = image.getInputStream()) {
            ValidationResource.correctedBuild(image);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .metadata(Map.of("filename", Objects.requireNonNull(image.getOriginalFilename())))
                    .contentLength(image.getSize())
                    .contentType(image.getContentType())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, image.getSize()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return key;
    }

    public void deleteImage(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

}