package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static faang.school.postservice.service.resource.ResourceType.IMAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    @Value("${app.resources.image.max-file-size-mb:5}")
    private int maxFileSizeMb;

    @Value("${app.resources.image.max-per-post:10}")
    private int maxImagesPerPost;

    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final S3Service s3Service;

    private long getMaxFileSizeBytes() {
        return (long) maxFileSizeMb * 1024 * 1024;
    }

    @Override
    @Transactional
    public List<ResourceDto> uploadResources(Long postId, List<MultipartFile> files) {
        validateImageFiles(files);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        validateImageCount(post, files.size());

        List<Resource> uploadedResources = files.stream()
                .map(file -> processAndUploadImage(post, file))
                .toList();

        post.getResources().addAll(uploadedResources);
        postRepository.save(post);
        log.info("Successfully uploaded {} images for post {}", files.size(), postId);

        return resourceMapper.toDtoList(uploadedResources);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceDto> getResourcesByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }

        List<Resource> images = resourceRepository.findByPostIdAndType(postId, ResourceType.IMAGE.name());

        log.info("Retrieved {} images for post {}", images.size(), postId);
        return resourceMapper.toDtoList(images);
    }

    @Override
    @Transactional
    public void deleteResource(Long resourceId) {
        log.info("Deleting resource {}", resourceId);
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

        Post post = resource.getPost();
        post.getResources().remove(resource);
        resourceRepository.delete(resource);

        s3Service.deleteFile(resource.getKey());

        postRepository.save(post);
        log.info("Resource {} deleted successfully", resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));
        byte[] fileBytes = s3Service.downloadFile(resource.getKey());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(detectMediaType(resource.getName()));
        headers.setContentDispositionFormData("attachment", resource.getName());
        headers.setContentLength(fileBytes.length);

        log.info("Downloaded resource {}", resourceId);
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DataValidationException("File cannot be empty");
        }

        if (file.getSize() > getMaxFileSizeBytes()) {
            throw new DataValidationException(
                    String.format("File %s exceeds maximum size of %d MB", file.getOriginalFilename(), maxFileSizeMb)
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new DataValidationException("Only image files are allowed");
        }
    }

    private void validateImageFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new DataValidationException("Files list cannot be empty");
        }

        if (files.size() > maxImagesPerPost) {
            throw new DataValidationException(
                    String.format("Maximum %d images allowed per upload", maxImagesPerPost)
            );
        }

        files.forEach(this::validateImageFile);
    }

    private void validateImageCount(Post post, int newImagesCount) {
        long currentImageCount = resourceRepository.countByPostIdAndType(post.getId(), ResourceType.IMAGE.name());

        if (currentImageCount + newImagesCount > maxImagesPerPost) {
            throw new DataValidationException(
                    String.format("Cannot upload %d images. Post already has %d images. Maximum %d images allowed per post.",
                            newImagesCount, currentImageCount, maxImagesPerPost)
            );
        }
    }

    private Resource processAndUploadImage(Post post, MultipartFile file) {
        try {
            byte[] processedImage = processImage(file.getBytes(), file.getContentType());
            String fileKey = generateFileKey(file.getOriginalFilename());

            s3Service.uploadFile(fileKey, processedImage, file.getContentType());
            Resource resource = Resource.builder()
                    .key(fileKey)
                    .name(file.getOriginalFilename())
                    .size((long) processedImage.length)
                    .type(IMAGE.name())
                    .post(post)
                    .build();

            Resource savedResource = resourceRepository.save(resource); // ← ЗДЕСЬ ИЗМЕНИТЬ

            log.debug("Uploaded image: {} -> {}", file.getOriginalFilename(), fileKey);
            return savedResource;

        } catch (IOException e) {
            log.error("Failed to upload image: {}", file.getOriginalFilename(), e);
            throw new DataValidationException("Failed to upload image: " + file.getOriginalFilename());
        }
    }

    private byte[] processImage(byte[] originalImage, String contentType) throws IOException {
        return originalImage;
    }

    private String generateFileKey(String originalFileName) {
        String extension = ".jpg";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return "posts/images/" + UUID.randomUUID() + extension;
    }

    private MediaType detectMediaType(String fileName) {
        if (fileName == null) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        if (fileName.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (fileName.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        } else if (fileName.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}