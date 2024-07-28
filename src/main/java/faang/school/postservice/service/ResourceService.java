package faang.school.postservice.service;


import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;


    @Transactional
    public void addResource(Long postId, MultipartFile file) {
        

    }

    public InputStream downloadResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() -> {
            log.error("Resource id: " + resourceId + "not found");
            return new EntityNotFoundException("Resource id: " + resourceId + "not found");
        });
        return s3Service.downloadFile(resource.getKey());
    }


}
