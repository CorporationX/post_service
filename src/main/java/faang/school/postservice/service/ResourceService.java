package faang.school.postservice.service;

import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public Resource createResource(Resource resource) {
        Resource saved = resourceRepository.save(resource);
        log.info("Resource saved. Key: {}", resource.getKey());
        return saved;
    }

    public Resource findResourceByKey(String key) {
        return resourceRepository.findByKey(key)
                .orElseThrow(() ->
                        new ResourceNotFoundException("There is no resource with key: " + key));
    }

    public void deleteResourceByKey(String key) {
        resourceRepository.deleteResourceByKey(key);
        log.info("Resource with key: {}. Was succesfully deleted", key);
    }


}
