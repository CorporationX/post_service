package faang.school.postservice.service.resource;

import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public Resource findById(Long id) {
      Optional<Resource> resource = resourceRepository.findById(id);
        return resource.orElseThrow(
                () -> new EntityNotFoundException("Resource service. Resource not found. id: " + id));
    }
}

