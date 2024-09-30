package faang.school.postservice.service.resource;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ResourceType;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.resource.s3.S3AudioService;
import faang.school.postservice.service.resource.s3.S3ImageService;
import faang.school.postservice.service.resource.s3.S3VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final S3ImageService s3ImageService;
    private final S3AudioService s3AudioService;
    private final S3VideoService s3VideoService;

    @Value("${resources.image.allowed-types}")
    private List<String> allowedImageTypes;
    @Value("${resources.audio.allowed-types}")
    private List<String> allowedAudioTypes;
    @Value("${resources.video.allowed-types}")
    private List<String> allowedVideoTypes;

    @Value("${resources.image.max-size}")
    private long maxImageSize;
    @Value("${resources.audio.max-size}")
    private long maxAudioSize;
    @Value("${resources.video.max-size}")
    private long maxVideoSize;

    @Value("${resources.image.max-in-post}")
    private int maxImageInPost;
    @Value("${resources.audio.max-in-post}")
    private int maxAudioInPost;
    @Value("${resources.video.max-in-post}")
    private int maxVideoInPost;

    @Transactional
    public Resource addFileToPost(MultipartFile file, Long postId) {
        validateFile(file);
        Post post = postRepository.findById(postId).orElseThrow();
        validateFileAmount(file, post);

        String mimeType = file.getContentType();
        ResourceType type = mimeToResType(mimeType);
        Resource resource = switch (type) {
            case IMAGE -> s3ImageService.addFileToStorage(file, post);
            case AUDIO -> s3AudioService.addFileToStorage(file, post);
            case VIDEO -> s3VideoService.addFileToStorage(file, post);
        };

        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource updateFileInPost(MultipartFile file, Long resourceId, Long postId) {
        validateFile(file);
        Post post = postRepository.findById(postId).orElseThrow();
        Resource oldResource = post.getResources().stream()
                .filter(res -> res.getId().equals(resourceId))
                .findFirst()
                .orElseThrow();

        String key = oldResource.getKey();
        Resource newResource = switch (oldResource.getType()) {
            case IMAGE -> s3ImageService.updateFileInStorage(key, file, post);
            case AUDIO -> s3AudioService.updateFileInStorage(key, file, post);
            case VIDEO -> s3VideoService.updateFileInStorage(key, file, post);
        };
        newResource.setId(resourceId);

        return resourceRepository.save(newResource);
    }

    @Transactional
    public void removeFileInPost(Long resourceId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        Resource resource = post.getResources().stream()
                .filter(res -> res.getId().equals(resourceId))
                .findFirst()
                .orElseThrow();

        String key = resource.getKey();
        switch (resource.getType()) {
            case IMAGE -> s3ImageService.removeFileInStorage(key);
            case AUDIO -> s3AudioService.removeFileInStorage(key);
            case VIDEO -> s3VideoService.removeFileInStorage(key);
        }

        resourceRepository.deleteById(resourceId);
    }

    private void validateFileAmount(MultipartFile file, Post post) {
        String mimeType = file.getContentType();
        ResourceType type = mimeToResType(mimeType);
        List<Resource> resources = post.getResources();

        switch (type) {
            case IMAGE -> validateAmount(resources, maxImageInPost, type);
            case AUDIO -> validateAmount(resources, maxAudioInPost, type);
            case VIDEO -> validateAmount(resources, maxVideoInPost, type);
        }
    }

    private void validateFile(MultipartFile file) {
        String mimeType = file.getContentType();
        ResourceType type = mimeToResType(mimeType);

        switch (type) {
            case IMAGE -> validateSize(file, maxImageSize);
            case AUDIO -> validateSize(file, maxAudioSize);
            case VIDEO -> validateSize(file, maxVideoSize);
        }
    }

    private void validateAmount(List<Resource> resources, int maxInPost, ResourceType resourceType) {
        long amountByType = resources.stream()
                .filter(resource -> resource.getType().equals(resourceType))
                .count();
        if (amountByType >= maxInPost) {
            throw new IllegalArgumentException("The number of " + resourceType.name() +
                    " cannot be more than " + maxInPost);
        }
    }

    private void validateSize(MultipartFile file, long maxSize) {
        if (file.getSize() > maxSize) {
            throw new FileException(file.getOriginalFilename() + " " + file.getContentType()
                    + ". Exceeded size of " + maxSize + " byte");
        }
    }

    private ResourceType mimeToResType(String mimeType) {
        if (allowedImageTypes.contains(mimeType)) {
            return ResourceType.IMAGE;

        } else if (allowedAudioTypes.contains(mimeType)) {
            return ResourceType.AUDIO;

        } else if (allowedVideoTypes.contains(mimeType)) {
            return ResourceType.VIDEO;

        } else {
            throw new FileException("Unsupported file format");
        }
    }
}
