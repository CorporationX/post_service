package faang.school.postservice.service.resource;

import faang.school.postservice.model.ImageResource;
import faang.school.postservice.repository.ImageResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ImageResourceRepository resourceRepository;

    public ImageResource saveResource(ImageResource resource) {
        ImageResource saved = resourceRepository.save(resource);
        log.info("Сохранён ресурс: id={}, key={}, size={}, type={}",
                saved.getId(), saved.getKey(), saved.getSize(), saved.getType());
        return saved;
    }

    public void deleteResource(ImageResource resource) {
        log.info("Удаление ресурса: id={}, key={}", resource.getId(), resource.getKey());
        resourceRepository.delete(resource);
        log.info("Ресурс с id={} успешно удалён", resource.getId());
    }
}
