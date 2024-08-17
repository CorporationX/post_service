package faang.school.postservice.service.s3;

import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Service
@RequiredArgsConstructor
public class MinioS3Client {
    private final S3Client s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());

        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(file.getBytes());
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromByteBuffer(byteBuffer));
        } catch (S3Exception | IOException e) {
            System.err.println("Failed to upload file: " + e.getMessage());
        }

        return Resource.builder()
                .key(key)
                .type(file.getContentType())
                .name(file.getOriginalFilename())
                .size(fileSize)
                .build();
    }

    public InputStream downloadFile(String key) {
        return s3Client.getObject(GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    public void deleteFIle(String fileKey, String smallFileKey) {
        s3Client.deleteObject(DeleteObjectRequest
                .builder()
                .bucket(bucketName)
                .key(fileKey)
                .build());

        s3Client.deleteObject(DeleteObjectRequest
                .builder()
                .bucket(bucketName)
                .key(smallFileKey)
                .build());
    }
}
