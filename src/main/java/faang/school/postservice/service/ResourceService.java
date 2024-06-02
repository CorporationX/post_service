package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.validation.MultipartFileValidator;
import faang.school.postservice.validation.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceRepository repository;
    private final MultipartFileValidator multipartFileValidator;
    private final S3Service s3Service;
    private final PostValidator postValidator;

    public Resource createResource(String key, MultipartFile file) {
        Resource resource = Resource.builder()
                .key(key)
                .size(file.getSize())
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .build();

        return repository.save(resource);
    }

    @Transactional
    public List<Resource> createResourceToPost(List<MultipartFile> files, Post post) {
        multipartFileValidator.validateFiles(files);

        String folder = String.valueOf(post.getId());
        List<Resource> resources = s3Service.uploadFiles(files, folder);

        for (Resource resource : resources) {
            Resource saved = repository.save(resource);
            saved.setPost(post);
        }

        return resources;
    }

    public List<Resource> deleteResources(List<Long> resourceIds) {
        List<Resource> resourcesToDelete = resourceIds.stream()
                .map(this::getResourceById)
                .toList();

        resourcesToDelete.forEach(resource -> s3Service.deleteFile(resource.getKey()));

        repository.deleteAll(resourcesToDelete);
        return resourcesToDelete;
    }


    private Resource getResourceById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Resource not found")
        );
    }
}
