package faang.school.postservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final AmazonS3 amazonS3Client;
    private final ResourceRepository resourceRepository;

    @Value("${services.s3.bucket-name}")
    private String bucketName;

    public Resource uploadImage(MultipartFile file, String folder, BufferedImage image) {
        Long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%s/%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        InputStream inputStream = getInputStream(image, file.getContentType());
        setContentLength(objectMetadata, inputStream);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
        amazonS3Client.putObject(putObjectRequest);
        return getResource(file, fileSize, key);
    }

    @Transactional
    public void deleteResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Resource with id %s not found", resourceId)));
        amazonS3Client.deleteObject(bucketName, resource.getKey());
        log.info("File {} deleted", resource.getKey());
    }

    private void setContentLength(ObjectMetadata objectMetadata, InputStream inputStream) {
        try {
            objectMetadata.setContentLength(inputStream.available());
        } catch (IOException e) {
            log.error("FileException", e);
            throw new FileException(e.getMessage());
        }
    }

    private Resource getResource(MultipartFile file, Long fileSize, String key) {
        return Resource.builder()
                .key(key)
                .size(fileSize)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .build();
    }

    private InputStream getInputStream(BufferedImage image, String contentType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, contentType, baos);
        } catch (IOException e) {
            log.error("FileException", e);
            throw new FileException(e.getMessage());
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
