package faang.school.postservice.service.resource;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.amazonS3.AmazonS3Service;
import faang.school.postservice.validator.image.ImageValidator;
import faang.school.postservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private final ImageValidator imageValidator;
    private final ResourceValidator resourceValidator;
    private final ResourceMapper resourceMapper;
    private final AmazonS3Service amazonS3Service;
    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;

    @Transactional
    public ResourceDto addResource(long postId, MultipartFile file) {
        Post post = getPost(postId);

        imageValidator.validateFileSize(file);
        imageValidator.validateFileCurrentPostImages(post);

        String folderName = String.format("post%d", postId);
        ResourceDto resourceDto = amazonS3Service.uploadFile(file, folderName);
        resourceDto.setPostId(postId);

        Resource resourceToSave = resourceMapper.toEntity(resourceDto);
        Resource savedResource = resourceRepository.save(resourceToSave);

        post.getResources().add(savedResource);
        postRepository.save(post);

        return resourceMapper.toDto(savedResource);
    }

    @Transactional
    public void deleteResource(long postId, long resourceId) {
        Post post = getPost(postId);

        int resourceIndex = resourceValidator.validateResourceInPost(post, resourceId);
        List<Resource> resourceList = post.getResources();
        String fileKey = resourceList.get(resourceIndex).getKey();

        log.info("Start delete the file with key = {}", fileKey);
        resourceList.remove(resourceIndex);
        post.setResources(resourceList);
        resourceRepository.deleteById(resourceId);
        postRepository.save(post);
        amazonS3Service.deleteFile(fileKey);
    }

    public List<ResourceDto> addResources(long postId, List<MultipartFile> files) {
        Post post = getPost(postId);
        resourceValidator.validateLimitResourcesPerPost(post, files.size());
        imageValidator.validateFilesSize(files);

        String folderName = String.format("post%d", postId);

        List<ResourceDto> resourceDtos = amazonS3Service.uploadFiles(files, folderName);
        resourceDtos.forEach(resourceDto -> resourceDto.setPostId(postId));

        List<Resource> resourcesToSave = resourceDtos.stream()
                .map(resourceMapper::toEntity)
                .toList();
        List<Resource> savedResources = resourceRepository.saveAll(resourcesToSave);

        post.getResources().addAll(savedResources);
        postRepository.save(post);

        return savedResources.stream().map(resourceMapper::toDto).toList();
    }


    private Post getPost(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.error("Couldn't find post in Repository {}", postId);
            return new EntityNotFoundException("Couldn't find post in Repository ID = " + postId);
        });
        return post;
    }

    @Transactional
    public InputStream downloadFile(long postId, long resourceId) {
        Post post = getPost(postId);
        int resourceIndex = resourceValidator.validateResourceInPost(post, resourceId);
        List<Resource> resourceList = post.getResources();
        String fileKey = resourceList.get(resourceIndex).getKey();

        return amazonS3Service.downloadFile(fileKey);
    }
}
