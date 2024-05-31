package faang.school.postservice.service;

import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceRepository repository;

    @Transactional
    public Resource createResource(String key, MultipartFile file) {
        Resource resource = Resource.builder()
                .key(key)
                .size(file.getSize())
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .build();

        return repository.save(resource);
    }
}
