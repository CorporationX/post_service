package faang.school.postservice.service.impl;

import faang.school.postservice.model.enums.ResourceStatus;
import faang.school.postservice.service.S3Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import faang.school.postservice.exception.MediaFileException;
import faang.school.postservice.model.entity.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final S3Client s3Client;

    @Value("${spring.services.s3.bucketName}")
    private String bucketName;

    @Override
    public List<Resource> uploadFiles(List<MultipartFile> files, String folder) {
        List<Resource> resources = new ArrayList<>();
        files.forEach(file -> {
            String key = generateKey(folder, file.getOriginalFilename());
            putObject(key, file);

            resources.add(Resource.builder()
                    .key(key)
                    .type(file.getContentType())
                    .name(file.getOriginalFilename())
                    .size(file.getSize())
                    .status(ResourceStatus.ACTIVE)
                    .build());
        });

        return resources;
    }

    @Override
    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String generateKey(String folder, String fileName) {
        String uniqueID = UUID.randomUUID().toString();
        return String.format("%s/%s_%s", folder, uniqueID, fileName);
    }

    private void putObject(String key, MultipartFile file) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (Exception ex) {
            log.error("Error uploading file to S3: {}", file.getOriginalFilename(), ex);
            throw new MediaFileException(String.format("Failed to upload file %s to S3", file.getOriginalFilename()));
        }
    }
}
