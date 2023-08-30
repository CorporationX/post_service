package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.model.resource.Resource;
import faang.school.postservice.util.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked" , havingValue = "true")
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long size = file.getSize();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(size);
        metadata.setContentType(file.getContentType());
        String key = String.format("%s/%s", folder, file.getOriginalFilename());
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            amazonS3.putObject(request);
        }catch (Exception e){
            throw new RuntimeException(ErrorMessage.FILE_EXCEPTION);
        }
        Resource resource = new Resource();
        resource.setName(file.getOriginalFilename());
        resource.setKey(key);
        resource.setSize(size);
        resource.setType(file.getContentType());
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        return resource;
    }

    public void deleteFile(String key) {
        amazonS3.deleteObject(bucketName, key);
    }

    public InputStream downloadFile(String key) {
        try {
            S3Object object = amazonS3.getObject(bucketName, key);
            return object.getObjectContent();
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FILE_EXCEPTION);
        }
    }
}
