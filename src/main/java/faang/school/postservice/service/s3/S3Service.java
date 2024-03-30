package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucket-name}")
    private String bucketName;

    public Resource uploadMedia(MultipartFile file, String folder) {
        Long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%s/%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        saveFile(bucketName, key, file, objectMetadata);
        return getResource(file, fileSize, key);
    }

    private void saveFile(String bucketName, String key, MultipartFile file, ObjectMetadata objectMetadata) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("FileException", e);
            throw new FileException(e.getMessage());
        }
    }

    private Resource getResource(MultipartFile file, Long fileSize, String key) {
        return Resource.builder()
                .key(key)
                .size(fileSize)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .build();
    }
}
