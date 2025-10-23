package faang.school.postservice.service.image;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private static final int MAX_FILE_SIZE_MB = 5;
    private static final long MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024;
    private static final int MAX_IMAGES_POST = 10;
    private static final String IMAGE_TYPE = "IMAGE";

    private final PostRepository postRepository;
    private final S3Service s3Service;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<Resource> uploadImages(Long postId, List<MultipartFile> files) {
        validateImageFiles(files);
        Post post = getPost(postId);
        validateImageCount(post, files.size());

        List<Resource> uploadedResources = files.stream()
                .map(file -> processAndUploadImage(post, file))
                .collect(Collectors.toList());

        postRepository.save(post);
        log.info("Successfully uploaded {} images for post {}", files.size(), postId);

        return uploadedResources.stream()
                .map(this::safeCopy)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> getResourcesByPostId(long postId) {
        Post post = getPost(postId);
        List<Resource> images = post.getResources().stream()
                .filter(resource -> IMAGE_TYPE.equals(resource.getType()))
                .collect(Collectors.toList());

        log.info("Retrieved {} images for post {}", images.size(), postId);
        return images.stream()
                .map(this::safeCopy)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Resource> deleteResource(long postId, long resourceId) {
        log.info("Deleting resource {} from post {}", resourceId, postId);

        Post post = getPost(postId);
        Resource resource = getResourceFromPost(post, resourceId);
        validateResourceOwnership(postId, resource);

        post.getResources().remove(resource);
        Resource managed = entityManager.contains(resource) ? resource : entityManager.merge(resource);
        entityManager.remove(managed);

        // файл удаляем после удаления записи
        s3Service.deleteFile(resource.getKey());

        postRepository.save(post);
        log.info("Resource {} deleted successfully from post {}", resourceId, postId);

        return post.getResources().stream()
                .map(this::safeCopy)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadResource(Long postId, Long resourceId) {
        Post post = getPost(postId);
        Resource resource = getResourceFromPost(post, resourceId);
        validateResourceOwnership(postId, resource);

        byte[] fileBytes = s3Service.downloadFile(resource.getKey());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(detectMediaType(resource.getName()));
        headers.setContentDispositionFormData("attachment", resource.getName());
        headers.setContentLength(fileBytes.length);

        log.info("Downloaded resource {} from post {}", resourceId, postId);
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    private void validateImageFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Files list cannot be empty");
        }

        if (files.size() > MAX_IMAGES_POST) {
            throw new IllegalArgumentException(
                    String.format("Maximum %d images allowed per upload", MAX_IMAGES_POST)
            );
        }
// для каждого элемента в списке files
        files.forEach(this::validateImageFile);
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    String.format("File %s exceeds maximum size of %d MB",
                            file.getOriginalFilename(), MAX_FILE_SIZE_MB)
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }

    private void validateImageCount(Post post, int newImagesCount) {
        long currentImageCount = post.getResources().stream()
                .filter(resource -> IMAGE_TYPE.equals(resource.getType()))
                .count();

        if (currentImageCount + newImagesCount > MAX_IMAGES_POST) {
            throw new IllegalArgumentException(
                    String.format("Cannot upload %d images. Post already has %d images. Maximum %d images allowed per post.",
                            newImagesCount, currentImageCount, MAX_IMAGES_POST)
            );
        }
    }

    private Resource processAndUploadImage(Post post, MultipartFile file) {
        try {
            // Генерируем ключ файла
            String fileKey = generateFileKey(file.getOriginalFilename());

            // Загружаем файл в S3 используя существующий метод
            s3Service.uploadFile(fileKey, file.getBytes(), file.getContentType());
            Resource resource = Resource.builder()
                    .key(fileKey)
                    .name(file.getOriginalFilename())
                    .size(file.getSize())
                    .type(IMAGE_TYPE)
                    .post(post)
                    .build();

            entityManager.persist(resource);
            post.getResources().add(resource);

            log.debug("Uploaded image: {} -> {}", file.getOriginalFilename(), fileKey);
            return resource;

        } catch (IOException e) {
            log.error("Failed to upload image: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload image: " + file.getOriginalFilename(), e);
        }
    }

    private String generateFileKey(String originalFileName) {
        String extension = ".jpg";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return "posts/images/" + UUID.randomUUID() + extension;
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
    }

    private Resource getResourceFromPost(Post post, Long resourceId) {
        return post.getResources().stream()
                .filter(resource -> resource.getId().equals(resourceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));
    }

    private void validateResourceOwnership(Long postId, Resource resource) {
        if (!resource.getPost().getId().equals(postId)) {
            throw new RuntimeException(
                    String.format("Resource %d not owned by post %d", resource.getId(), postId)
            );
        }
    }

    private Resource safeCopy(Resource original) {
        return Resource.builder()
                .id(original.getId())
                .key(original.getKey())
                .name(original.getName())
                .size(original.getSize())
                .type(original.getType())
                .createdAt(original.getCreatedAt())
                .post(null) // ← ЯВНО УСТАНОВИТЬ NULL ДЛЯ POST// НЕ копируем Post - это разрывает циклическую ссылку!
                .build();
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