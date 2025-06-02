package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.FileProcessException;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.S3Servce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class s3ServiceImpl implements S3Servce {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(ByteArrayInputStream file, String path) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/jpeg");
        objectMetadata.setContentLength(file.available());
        String key = String.format("%s resized_image.jpg", path);
        PutObjectRequest savedFile = new PutObjectRequest(bucketName, key, file, objectMetadata);
        amazonS3.putObject(savedFile);

        return Resource.builder()
                .key(key)
                .name(path + "_image.jpg")
                .size(file.available())
                .type("image/jpeg")
                .createdAt(LocalDateTime.now())
                .build();
    }

//    public URL getFileUrl(String fileKey) {
//        return amazonS3.getUrl(bucketName, fileKey);
//    }

    @Override
    public void deleteFile(String fileKey) {
        amazonS3.deleteObject(bucketName, fileKey);
    }

    @Override
    public InputStream downloadFile(String fileKey) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, fileKey);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Exception was thrown while downloading file", e);
            throw new FileProcessException("Error while downloading file with key %s from bucket %s"
                    .formatted(fileKey, bucketName));
        }
    }
}
