package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ResourceType;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final S3ImageService s3ImageService;

    @Value("${resources.max-image-in-post}")
    private int maxImageInPost;
    @Value("${resources.picture-allowed-types}")
    private List<String> allowedPictureFileType;

    @Transactional
    public List<Resource> addImagesToPost(List<MultipartFile> files, Long postId) {
        validateContentType(files);
        Post post = postRepository.findById(postId).orElseThrow();
        validateAmount(post, files);

        List<Resource> resources = s3ImageService.addFilesToStorage(files, post);

        return resourceRepository.saveAll(resources);
    }

    @Transactional
    public Resource updateImageInPost(MultipartFile file, Long resourceId, Long postId) {
        return null;
    }

    private void validateAmount(Post post, List<MultipartFile> files) {
        long imageAmountInPost = post.getResources().stream()
                .filter(resource -> resource.getType().equals(ResourceType.IMAGE))
                .count();

        if (maxImageInPost - imageAmountInPost < files.size()) {
            throw new IllegalArgumentException("Exceeded the allowed number of images. " +
                    "You can add a maximum of " + (maxImageInPost - imageAmountInPost) + " images.");
        }
    }

    private void validateContentType(List<MultipartFile> files) {
        long imageAmount = files.stream()
                .filter(file -> Objects.nonNull(file.getContentType()))
                .filter(file -> allowedPictureFileType.contains(file.getContentType()))
                .count();
        if (imageAmount != files.size()) {
            throw new IllegalArgumentException("Some file not .jpeg, .png, .webp image");
        }
    }
}
