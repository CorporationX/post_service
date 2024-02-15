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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    @Value("${post.content_to_post.max_amount}")
    private int maxAmountFiles;
    private final AmazonS3Service amazonService;
    private final ResourceRepository resourceRepository;
    private final PostService postService;
    private final ResourceMapper resourceMapper;
    private final PostValidator postValidator;

    public List<Resource> addFilesToPostAndSaveResources(List<MultipartFile> files, Post post) {
        /*List<Resource> postResources = Optional.ofNullable(post.getResources())
                .orElse(new ArrayList<>());*/
        int currentAmountPostFiles = post.getResources() == null ? 0 : post.getResources().size();
        if (currentAmountPostFiles + files.size() > maxAmountFiles) {
            throw new IllegalArgumentException("Post must contain 10 or less files");
        }

        List<Resource> savedResources = files.stream().map(file -> {
            String folderName = getFolderName(post.getId(), file.getContentType());
            Resource resource = amazonService.uploadFile(file, folderName);
            resource.setPost(post);
            return resource;
        }).toList();

        return resourceRepository.saveAll(savedResources);

/*        postResources.addAll(resourceRepository.saveAll(savedResources));
        return postResources;*/
    }

    private String getFolderName(long postId, String contentType) {
        return String.format("%s-%s", postId, contentType);
    }

    public ResourceDto deleteResource(long resourceId) {
        Resource resource = getResourceById(resourceId);
        postValidator.validateAccessToPost(resource.getPost());

        amazonService.deleteFile(resource.getKey());

        resourceRepository.delete(resource);
        return resourceMapper.toDto(resource);
    }

    public void deleteResources (Iterable<Resource> resources) {
        resourceRepository.deleteAll(resources);
    }

    public byte[] downloadResource(long id) {
        String key = getResourceById(id).getKey();
        byte[] bytes;

        try (InputStream inputStream = amazonService.downloadFile(key)) {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bytes;
    }

    public Resource getResourceById(long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Resource with id %s not found", id)));
    }

    public ResourceDto getResource (long id) {
        Resource resource = getResourceById(id);
        Post post = resource.getPost();
        postValidator.validateAccessToPost(post);

        return resourceMapper.toDto(resource);
    }

    public ResourceDto createResource(long postId, MultipartFile file) {
        Post post = postService.getPost(postId);
        postValidator.validateAccessToPost(post);

        Resource resource = amazonService.uploadFile(file, getFolderName(postId, file.getContentType()));
        resource.setPost(post);

        Resource savedResource = resourceRepository.save(resource);

        return resourceMapper.toDto(savedResource);
    }
}
