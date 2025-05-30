package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.S3Servce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class s3ServiceImpl implements S3Servce {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String folder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        String key = String.format("%s/%s", folder, file.getName());
        try {
            PutObjectRequest savedFile = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            amazonS3.putObject(savedFile);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }

        return Resource.builder()
                .key(key)
                .name(file.getName())
                .size(file.getSize())
                .type(file.getContentType())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public URL getFileUrl(String fileKey) {
        return amazonS3.getUrl(bucketName, fileKey);
    }

    @Override
    public void deleteFile(String fileKey) {
        amazonS3.deleteObject(bucketName, fileKey);
    }
}
