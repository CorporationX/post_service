package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.S3Servce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
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
        String key = String.format("%s/%s", path, "resized_image.jpg");
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

    @Override
    public URL getFileUrl(String fileKey) {
        return amazonS3.getUrl(bucketName, fileKey);
    }

    @Override
    public void deleteFile(String fileKey) {
        amazonS3.deleteObject(bucketName, fileKey);
    }
}
