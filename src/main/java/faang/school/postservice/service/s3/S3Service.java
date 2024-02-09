package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 clientAmazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            clientAmazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataValidationException("Error uploading file " + e.getMessage());
        }

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(fileSize);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setType(file.getContentType());
        resource.setName(file.getOriginalFilename());

        return resource;

    }
}
