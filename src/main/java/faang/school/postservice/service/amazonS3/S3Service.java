package faang.school.postservice.service.amazonS3;

import faang.school.postservice.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
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


//    public InputStream downloadFile(String key) {
//        GetObjectRequest request = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build();
//
//        InputStream inputStream = s3Client.getObject(request);
//
//
//    }
    //todo скачивание изображение




}
