package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("$(services.s3.bucketName)")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder){
        long fileSize = file.getSize();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(file.getContentType());
        String key = folder + file.getOriginalFilename();
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            amazonS3.putObject(request);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return Resource.builder()
                .key(key)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .size(fileSize)
                .build();
    }

    public void deleteFile(String key){
        amazonS3.deleteObject(bucketName, key);
    }

    public InputStream downloadFile(String key){
        try {
            return amazonS3.getObject(bucketName, key).getObjectContent();
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
