package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class S3ServiceImp implements S3Service {
    private final AmazonS3 s3Client;
    private final ResourceRepository resourceRepository;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFiles(MultipartFile file, byte[] bytes) {
        String fileKey = String.format("%s-%s", System.currentTimeMillis(), file.getOriginalFilename());
        long fileSize = file.getSize();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(file.getContentType());

        InputStream inputStream = new ByteArrayInputStream(bytes);
        s3Client.putObject(bucketName, fileKey, inputStream, metadata);

        Resource resource = new Resource();
        resource.setKey(fileKey);
        resource.setSize(fileSize);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setName(file.getOriginalFilename());
        return resource;
    }

    @Override
    public Resource deleteResource(Long deletedFileId) {
        Resource resource = getResource(deletedFileId);
        resourceRepository.deleteById(deletedFileId);
        s3Client.deleteObject(bucketName, resource.getKey());
        return resource;
    }

    private Resource getResource(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
    }
}
