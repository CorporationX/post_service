package faang.school.postservice.service.resource;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceService {
    @Value("${post.content_to_post.max_amount}")
    private int maxAmountFiles = 10;
    private final AmazonS3Service amazonService;
    private final ResourceRepository resourceRepository;

    public List<Resource> addFilesToPost(List<MultipartFile> files, Post post) {
        List<Resource> postResources = Optional.ofNullable(post.getResources())
                .orElse(new ArrayList<>());
        if (postResources.size() + files.size() > maxAmountFiles) {
            throw new IllegalArgumentException("Post must contain 10 or less files");
        }

        List<Resource> savedResources = files.stream().map(file -> {
            Resource resource = amazonService.uploadFile(file, "FOLDER");
            resource.setPost(post);
            return resource;
        }).toList();


        postResources.addAll(resourceRepository.saveAll(savedResources));
        return postResources;
    }

    public Resource deleteResourceToPost(long resourceId) {
        Resource resource = getResourceById(resourceId);
        amazonService.deleteFile(resource.getKey());
        resourceRepository.delete(resource);
        return resource;
    }

    public InputStream downloadResource(long id) {
        String key = getResourceById(id).getKey();
        return amazonService.downloadFile(key);
    }

    public Resource getResourceById(long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Resource with id %s not found", id)));
    }
}
