package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.S3.delete.DeleteImageS3ServiceImpl;
import faang.school.postservice.service.S3.upload.UploadImagesS3ServiceImpl;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostImagesService {

    private final ResourceService resourceService;
    private final ImageValidator imageValidator;
    private final PostService postService;
    private final DeleteImageS3ServiceImpl deleteImageS3Service;
    private final UploadImagesS3ServiceImpl uploadImagesS3Service;
    private final PostRepository postRepository;

    public void uploadPostImages(Long id, List<MultipartFile> images) {
        Post post = postService.findById(id);

        validateImages(images);

        List<String> keys = uploadImagesS3Service.uploadImages(images);

        List<Resource> resources = keys.stream().map((key) -> Resource.builder().key(key).build()).toList();
        post.getResources().addAll(resources);

        postRepository.save(post);
    }

    public void updatePostImages(Long id, List<MultipartFile> images) {
        Post post = postService.findById(id);
        List<Resource> postResources = post.getResources();

        validateImages(images);

        List<String> keys = uploadImagesS3Service.uploadImages(images);

        List<Resource> resources = keys.stream().map((key) -> Resource.builder().key(key).build()).toList();
        post.getResources().removeAll(postResources);
        post.getResources().addAll(resources);

        postRepository.save(post);
    }

    public void deleteImage(Long id) {
        Resource resource = resourceService.findById(id);
        deleteImageS3Service.deleteImage(resource.getKey());
    }

    private void checkListCapacity(List<MultipartFile> list) {
        if (list.size() > 10) {
            throw new RuntimeException("PostImageService. Amount images more 10");
        }
    }

    private void validateImages(List<MultipartFile> images) {
        checkListCapacity(images);
        images.forEach(imageValidator::checkImageSizeExceeded);
        images.forEach(imageValidator::changeFileDimension);
    }
}
