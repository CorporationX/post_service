package faang.school.postservice.service.outer;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        String fileOriginalName = file.getOriginalFilename();
        String fileContentType = file.getContentType();

        log.info("Start upload file with name {}", file.getOriginalFilename());

        String fileKey = String.format("%s/%d%s", folder, System.currentTimeMillis(), fileOriginalName);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(fileContentType);

        try {
            s3Client.putObject(bucketName, fileKey, file.getInputStream(), objectMetadata);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return Resource.builder()
                .key(fileKey)
                .size(fileSize)
                .createdAt(LocalDateTime.now())
                .name(fileOriginalName)
                .type(fileContentType)
                .build();
    }

    public List<Resource> uploadFiles(List<MultipartFile> files, String folder) {
        if (files.size() > 10) {
            log.error("Maximum 10 files");
            throw new DataValidationException("You can upload a maximum of 10 files.");
        }

        return files.stream()
                .map(file -> uploadFile(file, folder))
                .toList();
    }

    public void deleteFile(String fileKey) {
        log.info("Delete file with key {} ", fileKey);
        s3Client.deleteObject(bucketName, fileKey);
    }

    public Resource updateFile(String fileKey, MultipartFile file, String folder) {
        log.info("Update file with key {}, on file {}", fileKey, file.getOriginalFilename());
        deleteFile(fileKey);
        return uploadFile(file, folder);
    }

    public InputStream downloadFile(String fileKey) {
        log.info("Start download file with key {}", fileKey);
        S3Object s3Object = s3Client.getObject(bucketName, fileKey);
        return s3Object.getObjectContent();
    }
}
