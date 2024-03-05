package faang.school.postservice.service;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final PostRepository postRepository;

    @Transactional
    public List<ResourceDto> addResources(Long postId, List<MultipartFile> files) {
        Post post = getPostById(postId);
        String folder = getFolderName(postId, post.getContent());
        List<Resource> resources = new ArrayList<>();
        for (MultipartFile file : files) {
            Resource resource = s3Service.uploadFile(file, folder);
            resource.setPost(post);
            resources.add(resource);
        }

        resourceRepository.saveAll(resources);
        post.getResources().addAll(resources);
        postRepository.save(post);

        return resourceMapper.toListDto(resources);
    }

    public List<ResourceDto> deleteResources(long postId, List<Long> resourceIds) {
        Post post = getPostById(postId);
        List<Resource> resourcesToDelete = resourceIds.stream()
                .map(this::getResourceById)
                .toList();

        resourcesToDelete.forEach(resource -> s3Service.deleteFile(resource.getKey()));
        resourceRepository.deleteAll(resourcesToDelete);
        post.getResources().removeAll(resourcesToDelete);
        postRepository.save(post);

        return resourceMapper.toListDto(resourcesToDelete);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post not found"));
    }


    public InputStream downloadResource(long resourceId) {
        Resource resource = getResourceById(resourceId);
        return s3Service.downloadFile(resource.getKey());
    }

    private Resource getResourceById(long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(
                () -> new DataValidationException("Resource not " + "found"));
    }

    private String getFolderName(long postId, String contentType) {
        return String.format("%s-%s", postId, contentType);
    }

}
