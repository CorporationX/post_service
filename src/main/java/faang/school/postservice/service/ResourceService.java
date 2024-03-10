package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final AmazonS3Service amazonS3Service;
    private final PostValidator postValidator;
    private final ResourceValidator resourceValidator;
    private final ResourceRepository resourceRepository;
    private final PostMapper postMapper;
    private final ResourceMapper resourceMapper;
    private final PostRepository postRepository;

    private String getFolderName(long postId, String contentType) {
        return String.format("%s-%s", postId, contentType);
    }

    public List<ResourceDto> deleteResources(List<Long> resourceIds) {
        List<Resource> resourcesToDelete = resourceIds.stream()
                .map(this::validateAccessAndGetResource)
                .toList();

        resourcesToDelete.forEach(resource -> amazonS3Service.deleteFile(resource.getKey()));

        resourceRepository.deleteAll(resourcesToDelete);
        return resourcesToDelete.stream()
                .map(resourceMapper::toDto)
                .toList();
    }

    private Resource validateAccessAndGetResource(long id) {
        Resource resource = getResourceById(id);
        Post post = resource.getPost();
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();
        if (authorId != null) {
            postValidator.validateAuthor(authorId);
        }
        if (projectId != null) {
            postValidator.validateProject(projectId);
        }
        return resource;
    }

    public List<ResourceDto> createResources(Post post, List<MultipartFile> files) {
        PostDto postDto = postMapper.toDto(post);
        postValidator.validatePostOwnerExists(postDto);
        resourceValidator.validateFiles(postDto, files);

        List<Resource> resources = new ArrayList<>();
        for (MultipartFile file : files) {
            Resource resource = amazonS3Service.uploadFile(file, getFolderName(post.getId(), file.getContentType()));
            resource.setPost(post);
            resources.add(resource);
        }
        List<Resource> savedResources = resourceRepository.saveAll(resources);

        return savedResources.stream().map(resourceMapper::toDto).toList();
    }

    public List<ResourceDto> addResources(long postId, List<MultipartFile> files) {
        Post post = postRepository.findById(postId).orElseThrow(() ->     // Repository, т.к. идёт перекрест сервисов
                new EntityNotFoundException("Post not found"));
        return createResources(post, files);
    }

    public Resource getResourceById(long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Resource with id %s not found", resourceId)));
    }

    public byte[] downloadResource(long resourceId) {
        Resource resource = validateAccessAndGetResource(resourceId);

        String key = resource.getKey();
        byte[] file;

        try (InputStream inputStream = amazonS3Service.downloadFile(key)) {
            file = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
}
