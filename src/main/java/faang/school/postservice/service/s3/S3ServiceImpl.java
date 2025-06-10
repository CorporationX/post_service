package faang.school.postservice.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.service.S3Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 amazonS3;

    @Value("${s3.bucketName}")
    private String bucketName;

    @PostConstruct
    public void ensureBucketExists(){
        if (!amazonS3.doesBucketExistV2(bucketName)) amazonS3.createBucket(bucketName);
    }

    @Override
    public String uploadFile(MultipartFile file, String key) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        try {
            PutObjectRequest savedFile = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            amazonS3.putObject(savedFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileException(String.format("File saving failed: %s", e.getMessage()));
        }

        return key;
    }

    @Override
    public InputStream downloadFile(String fileKey) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, fileKey);
            return s3Object.getObjectContent();
        } catch (SdkClientException e) {
            throw new FileException(String.format("File not found: %s", e.getMessage()));
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        amazonS3.deleteObject(bucketName, fileKey);
    }

}
