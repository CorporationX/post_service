package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.exceptions.EntityNotFoundException;
import faang.school.postservice.exceptions.FileException;
import faang.school.postservice.mapper.ResourcePostMapper;
import faang.school.postservice.model.ImageType;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.minio.MinioServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResourcePostServiceImpl implements ResourcePostService {
    private final MinioServiceImpl minioService;
    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final ResourcePostMapper resourcePostMapper;
    private final ImageProcessServiceImpl imageProcessServiceImpl;
    @Value("${services.s3.posts.download.size.max}")
    private int MAX_SIZE_IMAGE;
    @Value("${services.s3.posts.upload.max-image-count}")
    private int MAX_IMAGE_COUNT;
    @Value("#'{${services.s3.posts.upload.format}'.split(',')}")
    private List<String> formatImage;


    @Transactional
    public List<ResourceDto> addResources(long postId, MultipartFile[] files, ImageType type) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("At least one file is required");
        }
        if (files.length > MAX_IMAGE_COUNT) {
            throw new IllegalArgumentException(
                    "Maximum number %d of downloaded images exceeded".formatted(MAX_IMAGE_COUNT));
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        List<ResourceDto> result = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                ResourceDto resourceDto = processSingleFile(post, file, type);
                result.add(resourceDto);
            } catch (IOException e) {
                log.error("Error processing file: {}", file.getOriginalFilename(), e);
                throw new FileException("Error processing file: " + file.getOriginalFilename());
            }
        }
        return result;
    }

    private ResourceDto processSingleFile(Post post, MultipartFile file, ImageType type) throws IOException {
        validateImageFile(post, file);
        String folder = "posts/" + post.getId();
        String contentType = file.getContentType();
        Resource minioResource = minioService.uploadImage(
                imageProcessServiceImpl.resizeImage(file, type),
                folder,
                file.getOriginalFilename(),
                contentType
        );
        Resource resourceEntity = Resource.builder()
                .post(post)
                .key(minioResource.getKey())
                .name(minioResource.getName())
                .type(minioResource.getType())
                .size(minioResource.getSize())
                .createdAt(LocalDateTime.now())
                .build();

        Resource savedResource = resourceRepository.save(resourceEntity);
        return resourcePostMapper.toDto(savedResource);
    }

    public List<String> getResource(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));
        String folder = "posts/%d".formatted(post.getId());
        return minioService.downloadImage(post.getResources().get(0).getKey(), folder);
    }

    public void deleteResource(Long postId, List<Long> resourceIds) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post exist"));
        List<Resource> resourcesToRemove = resourceRepository.findAllById(resourceIds);
        for (Resource resource : resourcesToRemove) {
            minioService.deleteImage(resource.getKey());
            post.getResources().remove(resource);
        }
        resourceRepository.deleteAll(resourcesToRemove);
    }

    private void validateImageFile(Post post, MultipartFile file) {
        if (file.getSize() > MAX_SIZE_IMAGE) {
            throw new IllegalArgumentException("Size exceeded 5MB image");
        }
        String formatImageString = formatImage.toString();
        if (post.getResources().size() > MAX_IMAGE_COUNT) {
            throw new DataValidationException("Maximum number of images exceeded");
        }
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new IllegalArgumentException("Your photo does not contain the required extension %s"
                    .formatted(formatImageString));
        }
        String substringFormat = originalFileName.substring(originalFileName.indexOf(".")).toLowerCase();
        if (!formatImage.contains(substringFormat)) {
            throw new IllegalArgumentException("Your photo does not contain the required extension %s"
                    .formatted(formatImageString));
        }
    }
}