package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


import java.util.UUID;
import java.io.InputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    private static final String BUCKET_NAME = "post-bucket"; 

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String mediaKey = String.format("%s/%s.%s", folder, UUID.randomUUID(), extension);
        
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileSize);
            metadata.setContentType(file.getContentType());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(mediaKey)
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(inputStream, fileSize);
            s3Client.putObject(putObjectRequest, requestBody);
            log.info("File uploaded successfully to S3: {}", mediaKey);
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new RuntimeException("Failed to upload file", e);
        }
        return Resource.builder()
                .key(mediaKey)
                .size(fileSize)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .build();
    }
}