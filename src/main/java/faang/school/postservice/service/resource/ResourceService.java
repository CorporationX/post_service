package faang.school.postservice.service.resource;


import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.validator.ResourceServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final ResizeService resizeService;
    private final ResourceServiceValidator resourceServiceValidator;

    @Transactional
    public ResponseEntity<String> addImages(Long postId, List<MultipartFile> imageFiles) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post " + postId + " not found");
                    return new EntityNotFoundException("Post " + postId + " not found");
                });

        imageFiles.forEach(imageFile -> {
            resourceServiceValidator.validateResourceSize(imageFile.getSize());
            resourceServiceValidator.checkIfFileAreImages(imageFile);
        });

        resourceServiceValidator.checkingThereEnoughSpaceInPostToImage(
                post.getResources().size(), imageFiles.size());

        List<MultipartFile> suitableImages = new ArrayList<>();
        imageFiles.forEach(image -> {
            resourceServiceValidator.validateResourceSize(image.getSize());
        });

        String folder = "post" + postId + "image";
        List<Resource> resources = s3Service.uploadFiles(suitableImages, folder);
        resources.forEach(resource -> {
            resource.setPost(post);
            resourceRepository.save(resource);
        });
        post.getResources().addAll(resources);
        postRepository.save(post);

        return ResponseEntity.ok("Resources added successfully");
    }

    @Transactional
    public ResponseEntity<String> addImage(Long postId, MultipartFile imageFile) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post " + postId + " not found");
                    return new EntityNotFoundException("Post " + postId + " not found");
                });
        resourceServiceValidator.validateResourceSize(imageFile.getSize());
        resourceServiceValidator.checkIfFileAreImages(imageFile);
        resourceServiceValidator.checkingThereEnoughSpaceInPostToImage(post.getResources().size(), 1);

        MultipartFile suitableImage = resizeFile(imageFile);

        String folder = "post" + postId + "image";
        Resource resource = s3Service.uploadFile(suitableImage, folder);

        resource.setPost(post);
        resourceRepository.save(resource);
        post.getResources().add(resource);
        postRepository.save(post);

        return ResponseEntity.ok("Resource added successfully");
    }

    public ResponseEntity<String> deleteResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> {
                    log.error("Resource id: " + resourceId + " not found");
                    return new EntityNotFoundException("Resource id: " + resourceId + " not found");
                });

        s3Service.deleteFile(resource.getKey());
        resourceRepository.deleteById(resourceId);
        return ResponseEntity.ok("Resource deleted successfully");
    }

    public InputStream downloadResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> {
                    log.error("Resource id: " + resourceId + " not found");
                    return new EntityNotFoundException("Resource id: " + resourceId + " not found");
                });
        return s3Service.downloadFile(resource.getKey());
    }

    private MultipartFile resizeFile(MultipartFile file) {
        try {
            return resizeService.resizeImage(file);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException("Failed to resize image: " + file.getOriginalFilename());
        }
    }
}
