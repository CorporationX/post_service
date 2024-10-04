package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.S3.DeleteFileS3Service;
import faang.school.postservice.service.S3.UploadFilesS3Service;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validator.postImages.PostImageValidator;
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
    private final ChangerDimension changerDimension;
    private final PostService postService;
    private final DeleteFileS3Service deleteImageS3Service;
    private final UploadFilesS3Service uploadImagesS3Service;
    private final PostRepository postRepository;
    private final PostImageValidator postImageValidator;


    public void uploadPostImages(Long id, List<MultipartFile> images) {
        Post post = postService.findById(id);

        validateImages(images);

        List<Resource> resources = uploadImagesS3Service.uploadFiles(images);

        post.getResources().addAll(resources);

        postRepository.save(post);
    }

    public void updatePostImages(Long id, List<MultipartFile> images) {
        Post post = postService.findById(id);
        List<Resource> postResources = post.getResources();

        validateImages(images);

        List<Resource> resources = uploadImagesS3Service.uploadFiles(images);

        post.getResources().removeAll(postResources);
        post.getResources().addAll(resources);

        postRepository.save(post);
    }

    public void deleteImage(Long id) {
        Resource resource = resourceService.findById(id);
        deleteImageS3Service.deleteFile(resource.getName());
    }

    private void validateImages(List<MultipartFile> images) {
        postImageValidator.checkListCapacity(images);
        images.forEach(postImageValidator::checkImageSizeExceeded);
        images.forEach(changerDimension::changeFileDimension);
    }
}
