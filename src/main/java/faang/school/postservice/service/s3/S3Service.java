package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.util.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "true")
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folder, byte[] bytes) {
        ObjectMetadata metadata = new ObjectMetadata();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        metadata.setContentLength(inputStream.available());
        metadata.setContentType(file.getContentType());
        String key = String.format("%s/%s/%s", folder, file.getOriginalFilename(), LocalDateTime.now());

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream, metadata);
            amazonS3.putObject(request);
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FILE_EXCEPTION);
        }

        return key;
    }

    public void deleteFile(String key) {
        amazonS3.deleteObject(bucketName, key);
    }

    public byte[] downloadFile(String key) {
        try {
            S3Object object = amazonS3.getObject(bucketName, key);
            return object.getObjectContent().readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FILE_EXCEPTION);
        }
    }
}
