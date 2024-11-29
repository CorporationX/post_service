package faang.school.postservice.service.amazons3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.ExceptionMessage;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.service.amazons3.processing.ImageProcessingService;
import faang.school.postservice.validator.file.FileValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class Amazons3ServiceImpl implements AmazonS3Service {
    private final AmazonS3 s3Client;
    private final FileValidator fileValidator;
    private final ImageProcessingService imageProcessingService;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public void uploadFile(MultipartFile file, String key) throws IOException {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
        }

        InputStream inputStream = file.getInputStream();
        if (fileValidator.contentIsImage(file)) {
            inputStream = imageProcessingService.optimizeImage(inputStream);
        }

        byte[] contentBytes = imageProcessingService.toByteArray(inputStream);
        long contentLength = contentBytes.length;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(contentLength);
        objectMetadata.setContentType(file.getContentType());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(contentBytes), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("when trying to add a file an error occurred {}", e.getMessage());
            throw new FileException(ExceptionMessage.FILE_EXCEPTION.getMessage());
        }
    }

    @Override
    public InputStream getFile(String fileId) {
        S3Object answer = s3Client.getObject(bucketName, fileId);
        return answer.getObjectContent();
    }

    @Override
    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (Exception e) {
            log.error("when trying to delete a file an error occurred {}", e.getMessage());
            throw new FileException(ExceptionMessage.FILE_EXCEPTION.getMessage());
        }
    }
}
