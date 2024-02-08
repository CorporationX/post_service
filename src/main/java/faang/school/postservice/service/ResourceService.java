package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostService postService;
    private final ResourceValidator resourceValidator;
    private final UserContext userContext;

    @Transactional
    public void addResource(Long postId, MultipartFile file) {
        Post post = postService.getPostById(postId);
        resourceValidator.validateUserIsPostAuthor(post.getAuthorId(), userContext.getUserId());
        resourceValidator.validateResourceLimit((post.getResources().size()));
        String folder = "files";
        Resource resource = s3Service.uploadFile(file, folder);
        log.info("Resource uploaded: {}", resource.getName());
        resource.setPost(post);
        resourceRepository.save(resource);
        log.info("Resource saved: {}", resource.getKey());
    }

    @Transactional
    public void deleteResource(Long postId, Long resourceId) {
        resourceValidator.validateUserIsPostAuthor(postService.getPostById(postId).getAuthorId(), userContext.getUserId());
        Resource resource = resourceRepository.findById(resourceId).orElseThrow();
        resourceValidator.validateResourceBelongsToPost(resource, postId);
        s3Service.deleteFile(resource.getKey());
        log.info("Resource deleted: {}", resource.getKey());
        resourceRepository.deleteById(resourceId);
    }
}
