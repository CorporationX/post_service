package faang.school.postservice.service;

import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public void saveResource(Resource resource) {
        Resource result = resourceRepository.save(resource);
        log.info("Resource with ID: {} saved", result.getId());
    }

    public void deleteResource(Long resourceId) {
        resourceRepository.deleteById(resourceId);
        log.info("Resource with ID: {} deleted", resourceId);
    }

    public Long findIdByKey(String key) {
        return resourceRepository.findIdByKey(key).orElseThrow(() -> new EntityNotFoundException("Resource with key '" + key + "' not found"));
    }
}
