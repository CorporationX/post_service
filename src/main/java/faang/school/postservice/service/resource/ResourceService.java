package faang.school.postservice.service.resource;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.ResourceEntity;
import faang.school.postservice.model.ResourceType;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.resource.minio.MinioAudioManager;
import faang.school.postservice.service.resource.minio.MinioImageManager;
import faang.school.postservice.service.resource.minio.MinioManager;
import faang.school.postservice.service.resource.minio.MinioVideoManager;
import faang.school.postservice.service.resource.validator.AudioFileValidator;
import faang.school.postservice.service.resource.validator.FileValidator;
import faang.school.postservice.service.resource.validator.ImageFileValidator;
import faang.school.postservice.service.resource.validator.VideoFileValidator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final MimeConverter mimeConverter;

    private final MinioImageManager minioImageManager;
    private final MinioAudioManager minioAudioManager;
    private final MinioVideoManager minioVideoManager;

    private final ImageFileValidator imageValidator;
    private final AudioFileValidator audioValidator;
    private final VideoFileValidator videoValidator;

    private final Map<ResourceType, MinioManager> managerByType = new HashMap<>();
    private final Map<ResourceType, FileValidator> validatorByType = new HashMap<>();

    @PostConstruct
    public void init() {
        managerByType.put(ResourceType.IMAGE, minioImageManager);
        managerByType.put(ResourceType.AUDIO, minioAudioManager);
        managerByType.put(ResourceType.VIDEO, minioVideoManager);

        validatorByType.put(ResourceType.IMAGE, imageValidator);
        validatorByType.put(ResourceType.AUDIO, audioValidator);
        validatorByType.put(ResourceType.VIDEO, videoValidator);
    }

    public ResourceEntity addFileToPost(MultipartFile file, Long postId) {
        String mimeType = file.getContentType();
        ResourceType type = mimeConverter.getType(mimeType);

        FileValidator validator = validatorByType.get(type);
        validator.validateSize(file);

        Post post = postRepository.findById(postId).orElseThrow();
        validator.validateAmount(type, post);

        MinioManager minioManager = managerByType.get(type);
        ResourceEntity resource = minioManager.addFileToStorage(file, post);

        return saveResource(resource);
    }

    public ResourceEntity updateFileInPost(MultipartFile file, Long resourceId) {
        String mimeType = file.getContentType();
        ResourceType type = mimeConverter.getType(mimeType);

        FileValidator validator = validatorByType.get(type);
        validator.validateSize(file);

        ResourceEntity oldResourceEntity = resourceRepository.findById(resourceId).orElseThrow();
        String key = oldResourceEntity.getKey();
        Post post = oldResourceEntity.getPost();

        MinioManager minioManager = managerByType.get(type);
        ResourceEntity resource = minioManager.updateFileInStorage(key, file, post);
        resource.setId(resourceId);

        return saveResource(resource);
    }

    public void removeFileInPost(Long resourceId) {
        ResourceEntity resource = resourceRepository.findById(resourceId).orElseThrow();

        String key = resource.getKey();
        MinioManager minioManager = managerByType.get(resource.getType());
        minioManager.removeFileInStorage(key);

        deleteResource(resourceId);
    }

    @Transactional
    private ResourceEntity saveResource(ResourceEntity resource) {
        return resourceRepository.save(resource);
    }

    @Transactional
    private void deleteResource(Long resourceId) {
        resourceRepository.deleteById(resourceId);
    }
}

