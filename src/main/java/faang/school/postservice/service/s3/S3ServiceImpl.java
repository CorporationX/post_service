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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Value("${spring.services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String folder) {
        String key = generateKey(folder, file.getOriginalFilename());
        putObject(key, file);

        return Resource.builder()
                .key(key)
                .type(file.getContentType())
                .name(file.getOriginalFilename())
                .size(file.getSize())
                .build();
    }

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
                    .build());
        });

        return resources;
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            return s3Client.getObject(bucketName, key).getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage(), key);
            throw new FileException("Failed to download file");
        }
    }

    private ObjectMetadata createObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }

    private String generateKey(String folder, String fileName) {
        return String.format("%s/%d%s", folder, System.currentTimeMillis(), fileName);
    }

    private void putObject(String key, MultipartFile file) {
        try {
            s3Client.putObject(
                    new PutObjectRequest(bucketName, key, file.getInputStream(), createObjectMetadata(file)));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileException(String.format("Failed to upload file %s to S3", file.getOriginalFilename()));
        }
    }
}
