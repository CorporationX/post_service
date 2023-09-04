package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3ServiceImp implements S3Service {
    private final AmazonS3 s3Client;
    private final ResourceRepository resourceRepository;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public List<Resource> uploadFiles(MultipartFile[] files) {
        List<Resource> resources = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileKey = String.format("%s-%s", System.currentTimeMillis(), file.getOriginalFilename());
            long fileSize = file.getSize();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileSize);
            metadata.setContentType(file.getContentType());
            try {
                s3Client.putObject(bucketName, fileKey, file.getInputStream(), metadata);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Resource resource = new Resource();
            resource.setKey(fileKey);
            resource.setSize(fileSize);
            resource.setCreatedAt(LocalDateTime.now());
            resource.setUpdatedAt(LocalDateTime.now());
            resource.setName(file.getOriginalFilename());

            resources.add(resource);
        }
        return resources;
    }

    @Override
    public List<Resource> deleteResource(List<Long> deletedFiles) {
        List<Resource> deletedResources = new ArrayList<>();

        for (Long id : deletedFiles) {
            Resource resource = getResource(id);
            resourceRepository.deleteById(id);

            s3Client.deleteObject(bucketName, resource.getKey());

            deletedResources.add(resource);
        }
        return deletedResources;
    }

    private Resource getResource(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
    }
}
