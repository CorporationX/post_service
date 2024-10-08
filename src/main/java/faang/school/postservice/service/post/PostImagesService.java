package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.S3.S3Service;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validator.postImages.PostImageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostImagesService {

    private final ResourceService resourceService;
    private final DimensionChanger dimensionChanger;
    private final PostService postService;
    private final S3Service S3Service;
    private final PostRepository postRepository;
    private final PostImageValidator postImageValidator;

    public void uploadPostImages(Long postId, List<MultipartFile> images) {
        Post post = postService.findById(postId);

        validateImages(images);

        List<Resource> resources = S3Service.uploadFiles(images, postId);

        resourceService.saveResources(resources);

        postRepository.save(post);
    }

    public void updatePostImages(Long postId, List<MultipartFile> images) {
        Post post = postService.findById(postId);
        List<Resource> postResources = post.getResources();

        validateImages(images);

        List<Resource> resources = S3Service.uploadFiles(images, postId);

        resourceService.deleteResources(postResources);

        resourceService.saveResources(resources);

        postRepository.save(post);
    }

    @Transactional
    public void deleteImage(Long resourceId) {
        Resource resource = resourceService.findById(resourceId);

        resourceService.deleteResource(resourceId);

        S3Service.deleteFile(resource.getName());
    }

    private void validateImages(List<MultipartFile> images) {
        postImageValidator.checkListCapacity(images);
        images.forEach(postImageValidator::checkImageSizeExceeded);
        images.forEach(dimensionChanger::changeFileDimension);
    }
}
