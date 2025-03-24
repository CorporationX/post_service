package faang.school.postservice.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.File;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public File uploadFile(MultipartFile file, String folder) {
        UploadedFileInfo uploadFile = uploadToS3(file, folder);

        return File.builder()
                .key(uploadFile.key())
                .size(uploadFile.fileSize())
                .createdAt(LocalDateTime.now())
                .type(uploadFile.fileType())
                .build();
    }

    public Resource uploadResource(MultipartFile file, String folder) {
        UploadedFileInfo uploadObject = uploadToS3(file, folder);

        return Resource.builder()
                .key(uploadObject.key())
                .size(uploadObject.fileSize())
                .createdAt(LocalDateTime.now())
                .type(uploadObject.fileType())
                .name(file.getOriginalFilename())
                .build();
    }

    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
            log.info("Файл {} удален", key);
        } catch (AmazonServiceException e) {
            log.error("Ошибка сервиса AWS при удалении файла {}: {}", key, e.getMessage());
        } catch (SdkClientException e) {
            log.error("Ошибка клиента при удалении файла {}: {}", key, e.getMessage());
        }
    }

    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (AmazonServiceException e) {
            throw new EntityNotFoundException("Файл " + key + " не найден");
        }
    }

    private UploadedFileInfo uploadToS3(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        String fileType = file.getContentType();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(fileType);
        String key = String.format("%s/%d-%s", folder, System.currentTimeMillis(), file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return new UploadedFileInfo(fileSize, fileType, key);
    }

    private record UploadedFileInfo(long fileSize, String fileType, String key) {
    }
}
