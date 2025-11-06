package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.dto.resource.ResourceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Value(value = "${services.s3.bucketName}")
    private String bucketName;
    @Override
    public ResourceDto uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%s", folder, UUID.randomUUID());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return new ResourceDto(
                key,
                fileSize,
                file.getOriginalFilename(),
                file.getContentType(),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Override
    public void deleteFile(String key) {

    }

    @Override
    public InputStream downloadFile(String key) {
        return null;
    }
}
