package faang.school.postservice.service.resource;


import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final ResizeService resizeService;


    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024; //5mb

    @Transactional
    public void addResource(Long postId, MultipartFile file) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post " + postId + " not found");
                    return new EntityNotFoundException("Post " + postId + " not found");
                });

        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("File size exceeds the 5MB limit");
            throw new FileException("File size exceeds the 5MB limit");
        }

        MultipartFile resizedFile;
        try {
            resizedFile = resizeService.resizeImage(file);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException("Failed to resize image: " + file.getOriginalFilename());
        }

        String folder = "post" + post.getId();
        Resource resource = s3Service.uploadFile(resizedFile, folder);
        resource.setPost(post);
        resourceRepository.save(resource);
        post.getResources().add(resource);
        postRepository.save(post);
    }

    public InputStream downloadResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() -> {
            log.error("Resource id: " + resourceId + "not found");
            return new EntityNotFoundException("Resource id: " + resourceId + "not found");
        });
        return s3Service.downloadFile(resource.getKey());
    }


}
