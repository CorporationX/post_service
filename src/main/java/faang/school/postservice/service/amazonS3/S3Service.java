package faang.school.postservice.service.amazonS3;

import faang.school.postservice.dto.comment.CommentResponseImageDto;
import faang.school.postservice.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${minio.bucket}")
    private String bucketName;

    @Transactional
    public String uploadFile(String folder, MultipartFile file) {
        String key = String.format("%s/%s-%d", folder, file.getOriginalFilename(), System.currentTimeMillis());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentLength(file.getSize())
                .contentType(file.getContentType())
                .metadata(Map.of("file-name", Objects.requireNonNull(file.getOriginalFilename())))
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e) {
            log.error("File upload failed. File: {}, Size: {} bytes. Error: {}",
                    file.getName(), file.getSize(), e.getMessage(), e);
            throw new S3Exception("File upload failed for: " + file.getName());
        }
        return key;
    }

    @Transactional
    public void deleteFile(String key) {
        s3Client.deleteObject(request -> request.bucket(bucketName).key(key));
    }


    public CommentResponseImageDto downloadFile(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseInputStream<GetObjectResponse> responseStream = s3Client.getObject(request);
            GetObjectResponse response = responseStream.response();

            if (!response.hasMetadata()) {
                log.error("missing metadata");
                throw new S3Exception("missing metadata");
            }

            Map<String, String> metadata = response.metadata();

            return CommentResponseImageDto.builder()
                    .fileName(metadata.getOrDefault("filename", key))
                    .contentType(response.contentType())
                    .contentLength(response.contentLength())
                    .resource(new InputStreamResource(responseStream))
                    .build();

        } catch (S3Exception e) {
            log.error("File download failed. Key: {}. Error: {}", key, e.getMessage(), e);
            throw new S3Exception("File download failed for key: " + key);
        }
    }
}
