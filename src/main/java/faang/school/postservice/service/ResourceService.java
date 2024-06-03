package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.AmazonS3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final AmazonS3Service amazonS3Service;
    private final PostService postService;
    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Transactional
    public List<ResourceDto> addResources(long postId, List<MultipartFile> files) {
        log.info("Trying to add files to a post, with ID {}.", postId);
        Post post = postService.findById(postId);

        String folder = String.format("%s", post.getId());
        List<Resource> resources = amazonS3Service.uploadFiles(files, folder);
        post.setResources(resources);
        resourceRepository.saveAll(resources);
        postRepository.save(post);

        List<ResourceDto> resourceDtos = resources.stream()
                .map(resourceMapper::toDto)
                .toList();
        log.info("Resources have been successfully added to the post, with ID {}.", postId);

        return resourceDtos;
    }

    public void deleteResource(long postId, long resourceId) {
        Post post = postService.findById(postId);
        Resource resource = resourceRepository.getReferenceById(resourceId);

        post.getResources().stream()
                .filter(r -> resourceId == resource.getId())
                .findFirst()
                .orElseThrow(() -> {
                    log.info("");
                    return new EntityNotFoundException("");
                });

        post.getResources().remove(resource);
        postRepository.save(post);
        resourceRepository.delete(resource);
        amazonS3Service.deleteFile(resource.getKey());
    }

    public void updateResource() {

    }

    public void downloadFile() {

    }
}
