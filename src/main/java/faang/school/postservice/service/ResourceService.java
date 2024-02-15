package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    @Value("${post.content_to_post.max_amount}")
    private int maxAmountFiles;
    private final AmazonS3Service amazonService;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final PostValidator postValidator;

    private String getFolderName(long postId, String contentType) {
        return String.format("%s-%s", postId, contentType);
    }

    public List<ResourceDto> deleteResources(List<Long> resourceIds) {
        List<Resource> resourcesToDelete = resourceIds.stream()
                .map(this::validateAccessAndGetResource)
                .toList();

        resourcesToDelete.forEach(resource -> amazonService.deleteFile(resource.getKey()));

        resourceRepository.deleteAll(resourcesToDelete);
        return resourcesToDelete.stream()
                .map(resourceMapper::toDto)
                .toList();
    }

    public byte[] downloadResource(long resourceId) {
        Resource resource = validateAccessAndGetResource(resourceId);

        String key = resource.getKey();
        byte[] bytes;

        try (InputStream inputStream = amazonService.downloadFile(key)) {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bytes;
    }

    public ResourceDto getResource(long resourceId) {
        Resource resource = validateAccessAndGetResource(resourceId);

        return resourceMapper.toDto(resource);
    }

    private Resource validateAccessAndGetResource(long id) {
        Resource resource = getResourceById(id);
        Post post = resource.getPost();
        postValidator.validateAccessToPost(post.getAuthorId(), post.getProjectId());
        return resource;
    }

    public List<ResourceDto> createResources(Post post, List<MultipartFile> files) {
        postValidator.validateAccessToPost(post.getAuthorId(), post.getProjectId());
        if (post.getResources().size() + files.size() > maxAmountFiles) {
            throw new IllegalArgumentException("You can upload only 10 files or less");
        }

        List<Resource> resources = new ArrayList<>();
        files.forEach(file -> {
            Resource resource = amazonService.uploadFile(file, getFolderName(post.getId(), file.getContentType()));
            resource.setPost(post);
            resources.add(resource);
        });

        List<Resource> savedResources = resourceRepository.saveAll(resources);

        return savedResources.stream()
                .map(resourceMapper::toDto)
                .toList();
    }

    public Resource getResourceById(long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Resource with id %s not found", resourceId)));
    }
}
