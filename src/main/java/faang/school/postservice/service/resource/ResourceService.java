package faang.school.postservice.service.resource;


import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.validator.ResourceServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final ResizeService resizeService;
    private final ResourceServiceValidator resourceServiceValidator;
    private final ResourceMapper resourceMapper;

    @Transactional
    public List<ResourceDto> addImages(Long postId, List<MultipartFile> imageFiles) {
        Post post = getPostById(postId);
        resourceServiceValidator.validAddImages(imageFiles, post.getResources());
        List<MultipartFile> suitableImages = resizeFiles(imageFiles);

        List<Resource> resources = s3Service.uploadFiles(
                suitableImages, createFolder(postId, imageFiles.stream().findAny().get().getContentType()));
        resources.forEach(resource -> {
            resource.setPost(post);
            resourceRepository.save(resource);
        });

        post.getResources().addAll(resources);
        postRepository.save(post);

        return resourceMapper.resourceListToResourceDtoList(resources);
    }

    @Transactional
    public ResourceDto addImage(Long postId, MultipartFile imageFile) {
        Post post = getPostById(postId);
        resourceServiceValidator.validAddImage(imageFile, post.getResources());

        MultipartFile suitableImage = resizeFile(imageFile);
        Resource resource = s3Service.uploadFile(
                suitableImage, createFolder(postId, imageFile.getContentType()));

        resource.setPost(post);
        resource = resourceRepository.save(resource);
        post.getResources().add(resource);
        postRepository.save(post);

        return resourceMapper.resourceToResourceDto(resource);
    }

    public ResourceDto deleteResource(Long resourceId) {
        Resource resource = getResourceById(resourceId);

        s3Service.deleteFile(resource.getKey());
        resourceRepository.deleteById(resourceId);

        return resourceMapper.resourceToResourceDto(resource);
    }

    private MultipartFile resizeFile(MultipartFile file) {
        try {
            return resizeService.resizeImage(file);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException("Failed to resize image: " + file.getOriginalFilename());
        }
    }

    private List<MultipartFile> resizeFiles(List<MultipartFile> files) {
        List<MultipartFile> resizedFiles = new ArrayList<>();

        files.forEach(file -> {
            try {
                resizedFiles.add(resizeService.resizeImage(file));
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new FileException("Failed to resize image: " + file.getOriginalFilename());
            }
        });
        return resizedFiles;
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error(String.format("Post %d not found", postId));
                    return new EntityNotFoundException(String.format("Post %d not found", postId));
                });
    }

    private Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> {
                    log.error(String.format("Resource id: %d not found", resourceId));
                    return new EntityNotFoundException(String.format("Resource id: %d not found", resourceId));
                });
    }

    private String createFolder(Long postId, String fileType) {
        return String.format("Post%s%s", postId, fileType.replaceAll("/.*$", ""));
    }
}
