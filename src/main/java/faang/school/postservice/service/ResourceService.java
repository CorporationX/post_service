package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final PostRepository postRepository;
    private final AmazonS3Service amazonS3Service;
    private final ResourceRepository resourceRepository;
    private final PostService postService;
    private final ResourceMapper resourceMapper;
    @Value("${services.s3.maxFilesAmount}")
    private int maxFilesAmount;

    @Transactional
    public List<ResourceDto> addResource(long postId, List<MultipartFile> files) {
        Post post = postService.searchPostById(postId);
        if (post.getResources().size() == maxFilesAmount) {
            log.info("There are already {} pictures in the post", maxFilesAmount);
            throw new DataValidationException("The maximum number of images for the post has been exceeded");
        }

        List<Resource> resources = new ArrayList<>();
        files.forEach(file -> {
            String folder = post.getId() + "/" + file.getName();
            Resource resource = amazonS3Service.uploadFile(file, folder);
            log.info("File {} upload", resource.getName());
            resource.setPost(post);
            resources.add(resource);
            post.getResources().add(resource);
            log.info("File {} saved", resource.getName());
        });

        List<Resource> newResources = resourceRepository.saveAll(resources);
        postRepository.save(post);
        return newResources.stream()
                .map(resourceMapper::toDto)
                .toList();
    }


    @Transactional
    public void deleteResource(long postId, long resourceId) {
        Post post = postService.searchPostById(postId);
        Resource resource = resourceRepository.getReferenceById(resourceId);
        Resource optionalResource = post.getResources().stream()
                .filter(resource1 -> resource.getId() == resourceId)
                .findFirst()
                .orElseThrow(() -> {
                    log.info("Resource is empty");
                    throw new EntityNotFoundException("Resource with id " + resourceId + " does not belong to post with id " + postId);
                });
        post.getResources().remove(resource);
        postRepository.save(post);
        resourceRepository.delete(resource);
        String key = resource.getKey();
        log.info("File {} delete", resource.getName());
        amazonS3Service.deleteFile(key);
    }
}
