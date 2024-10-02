package faang.school.postservice.service.resource;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ResourceType;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.resource.minio.MinioAudioManager;
import faang.school.postservice.service.resource.minio.MinioImageManager;
import faang.school.postservice.service.resource.minio.MinioVideoManager;
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
    private final MinioImageManager minioImageManager;
    private final MinioAudioManager minioAudioManager;
    private final MinioVideoManager minioVideoManager;
    private final MimeConverter mimeConverter;

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
        ResourceType type = mimeConverter.getType(mimeType);
        Resource resource = switch (type) {
            case IMAGE -> minioImageManager.addFileToStorage(file, post);
            case AUDIO -> minioAudioManager.addFileToStorage(file, post);
            case VIDEO -> minioVideoManager.addFileToStorage(file, post);
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
            case IMAGE -> minioImageManager.updateFileInStorage(key, file, post);
            case AUDIO -> minioAudioManager.updateFileInStorage(key, file, post);
            case VIDEO -> minioVideoManager.updateFileInStorage(key, file, post);
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
            case IMAGE -> minioImageManager.removeFileInStorage(key);
            case AUDIO -> minioAudioManager.removeFileInStorage(key);
            case VIDEO -> minioVideoManager.removeFileInStorage(key);
        }

        resourceRepository.deleteById(resourceId);
    }

    private void validateFileAmount(MultipartFile file, Post post) {
        String mimeType = file.getContentType();
        ResourceType type = mimeConverter.getType(mimeType);
        List<Resource> resources = post.getResources();

        switch (type) {
            case IMAGE -> validateAmount(resources, maxImageInPost, type);
            case AUDIO -> validateAmount(resources, maxAudioInPost, type);
            case VIDEO -> validateAmount(resources, maxVideoInPost, type);
        }
    }

    private void validateFile(MultipartFile file) {
        String mimeType = file.getContentType();
        ResourceType type = mimeConverter.getType(mimeType);

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
}

