package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.resource.Resource;
import faang.school.postservice.model.resource.ResourceType;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.image.ImageProcessor;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.validator.ResourceValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ImageProcessor imageProcessor;
    private final ResourceValidator resourceValidator;
    private final ResourceMapper resourceMapper;

    @Override
    @Transactional
    public List<ResourceDto> uploadResourcesForPost(List<MultipartFile> files, Long postId) {
        resourceValidator.validatePostExists(postId);
        resourceValidator.validateFiles(files);
        resourceValidator.validateFileLimit(postId, files.size());

        List<Resource> resources = new ArrayList<>();
        List<String> keysForRollback = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                MultipartFile processedFile = processFileIfNeeded(file);
                Resource resource = uploadFile(processedFile);
                resource.setPostId(postId);
                Resource savedResource = resourceRepository.save(resource);
                resources.add(savedResource);
                keysForRollback.add(savedResource.getKey());
            } catch (Exception e) {
                log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                deleteUploadedFiles(keysForRollback);
                throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
            }
        }

        return resources.stream()
                .map(resourceMapper::toResourceDto)
                .toList();
    }

    @Override
    @Transactional
    public void updatePostResources(Long postId, List<MultipartFile> newFiles, List<Long> filesToDelete) {
        resourceValidator.validatePostExists(postId);

        List<ResourceDto> newResources = new ArrayList<>();
        if (newFiles != null && !newFiles.isEmpty()) {
            int filesToDeleteCount = filesToDelete != null ? filesToDelete.size() : 0;
            resourceValidator.validateFileLimitOnUpdate(postId, newFiles.size(), filesToDeleteCount);
            newResources = uploadResourcesForPost(newFiles, postId);
        }

        if (filesToDelete != null && !filesToDelete.isEmpty()) {
            try {
                deleteResources(postId, filesToDelete);
            } catch (Exception e) {
                List<String> keysToDelete = newResources.stream()
                        .map(ResourceDto::key)
                        .toList();
                deleteUploadedFiles(keysToDelete);
                throw new RuntimeException("Failed to delete old files after successful upload", e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceDto> getResourcesByPostId(Long postId) {
        List<Resource> resources = resourceRepository.findByPostId(postId);

        return resources.stream()
                .map(resourceMapper::toResourceDto)
                .toList();
    }

    @Override
    @Transactional
    public void deletePostResources(Long postId) {
        resourceValidator.validatePostExists(postId);

        List<Resource> resources = resourceRepository.findByPostId(postId);

        if (resources.isEmpty()) {
            log.info("No resources found for post ID: {}", postId);
            return;
        }

        log.info("Deleting {} resources for post ID: {}", resources.size(), postId);

        List<String> keysToDelete = resources.stream()
                .map(Resource::getKey)
                .toList();

        deleteUploadedFiles(keysToDelete);
        resourceRepository.deleteByPostId(postId);

        log.info("Successfully deleted all resources for post ID: {}", postId);
    }

    @Transactional
    public void deleteResources(Long postId, List<Long> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return;
        }

        List<Resource> resources = (List<Resource>) resourceRepository.findAllById(resourceIds);

        for (Resource resource : resources) {
            if (!resource.getPostId().equals(postId)) {
                throw new IllegalArgumentException(
                        "Resource with id " + resource.getId() + " does not belong to post " + postId
                );
            }
        }

        List<String> keysToDelete = resources.stream()
                .map(Resource::getKey)
                .toList();

        deleteUploadedFiles(keysToDelete);
        resourceRepository.deleteAllById(resourceIds);
    }


    private MultipartFile processFileIfNeeded(MultipartFile file) {
        if (isImage(file) && needsCompression(file)) {
            return imageProcessor.compressImage(file);
        }
        return file;
    }

    private boolean isImage(MultipartFile file) {
        return file.getContentType() != null && file.getContentType().startsWith("image/");
    }

    private boolean needsCompression(MultipartFile file) {
        return imageProcessor.needsCompression(file);
    }


    private Resource uploadFile(MultipartFile file) {
        String key = s3Service.uploadFile(file);

        return Resource.builder()
                .key(key)
                .size(file.getSize())
                .name(file.getOriginalFilename())
                .type(determineResourceType(file))
                .build();
    }

    private void deleteUploadedFiles(List<String> keys) {
        for (String key : keys) {
            try {
                s3Service.deleteFile(key);
            } catch (Exception e) {
                log.error("Failed to cleanup file during rollback: {}", key, e);
            }
        }
    }

    private ResourceType determineResourceType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return ResourceType.IMAGE;
            }
            if (contentType.startsWith("video/")) {
                return ResourceType.VIDEO;
            }
            if (contentType.startsWith("audio/")) {
                return ResourceType.AUDIO;
            }
        }
        return ResourceType.IMAGE;
    }
}
