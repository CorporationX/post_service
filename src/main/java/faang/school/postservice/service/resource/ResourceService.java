package faang.school.postservice.service.resource;

import faang.school.postservice.dto.image.ImageResource;
import faang.school.postservice.entity.resource.Resource;
import faang.school.postservice.model.resource.ImageResources;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @Transactional
    public Resource saveResource(Resource resource) {
        Resource saved = resourceRepository.save(resource);
        log.info("Сохранён ресурс: id={}, key={}, size={}, type={}",
                saved.getId(), saved.getKey(), saved.getSize(), saved.getType());
        return saved;
    }

    @Transactional
    public void deleteResource(Resource resource) {
        log.info("Удаление ресурса: id={}, key={}", resource.getId(), resource.getKey());
        resourceRepository.delete(resource);
        log.info("Ресурс с id={} успешно удалён", resource.getId());
    }

    @Transactional
    public ImageResources uploadImageResources(ImageResource imageStorage) {
        Resource imageResource = new Resource();
        imageResource.setKey(imageStorage.fileKey());
        imageResource.setName(imageStorage.fileName());
        imageResource.setType(imageStorage.contentType());
        imageResource.setSize(imageStorage.size());

        resourceRepository.save(imageResource);

        Resource previewResource = new Resource();
        previewResource.setKey(imageStorage.previewKey());
        previewResource.setName("preview_" + imageStorage.fileName());
        previewResource.setType(imageStorage.contentType());
        previewResource.setSize(imageStorage.size());

        resourceRepository.save(previewResource);

        return new ImageResources(imageResource, previewResource);
    }
}
