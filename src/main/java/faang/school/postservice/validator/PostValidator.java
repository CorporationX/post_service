package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClientMock;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.utils.ImageProcessingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {
    private final UserValidator userValidator;
    @Value("${post.images.max-to-upload}")
    private int imagesMaxNumber;

    private final ResourceRepository resourceRepository;
    private final UserServiceClientMock userServiceClient;
    private final ProjectServiceClientMock projectServiceClient;

    public void validateCreatePost(Post post) {
        checkInputAuthorOrProject(post);

        if (post.getVerificationStatus() == null) {
            post.setVerificationStatus(VerificationPostStatus.UNVERIFIED);
        }

        if (post.getAuthorId() != null) {
            userValidator.validateUserExists(post.getAuthorId());
        }

        if (post.getProjectId() != null) {
            checkProjectExists(post.getProjectId());
        }
    }

    public void checkInputAuthorOrProject(Post post) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();

        if (authorId == null && projectId == null) {
            throw new ValidationException("AuthorId or projectId must be filled in");
        }

        if (authorId != null && projectId != null) {
            throw new ValidationException("Specify either authorId or projectId to create a post");
        }
    }

    public void validateImagesToUpload(Long postId, List<MultipartFile> images) {
        if (isEmpty(images)) {
            throw new ValidationException("List of images to upload to post %s cannot be null or empty", postId);
        }

        if (images.size() > imagesMaxNumber) {
            throw new ValidationException("Max number of images to upload to post is %s", imagesMaxNumber);
        }

        List<Resource> existedImages = resourceRepository.findAllByPostId(postId);
        if (existedImages.size() + images.size() > imagesMaxNumber) {
            throw new ValidationException("Post %s already has %s images. You cannot add %s more. Max size is %s",
                    postId, existedImages.size(), images.size(), imagesMaxNumber);
        }

        images.forEach(image -> validateImageToUpload(postId, image));
    }

    private void checkUserExists(Long userId) {
        try {
            if (userServiceClient.getUser(userId) != null) {
                return;
            }
        } catch (RuntimeException ignore) {
        }

        throw new ValidationException("User id=%s not exist", userId);
    }

    private void checkProjectExists(Long projectId) {
        try {
            if (projectServiceClient.getProject(projectId) != null) {
                return;
            }
        } catch (RuntimeException ignore) {
        }

        throw new ValidationException("Project id=%s not exist", projectId);
    }

    private void validateImageToUpload(Long postId, MultipartFile image) {
        String imageName = image.getOriginalFilename();
        if (image.getSize() > POST_IMAGES.getMaxSize()) {
            throw new ValidationException("You cannot upload file %s to post %s with size %s. Max size is %s",
                    imageName, postId, image.getSize(), POST_IMAGES.getMaxSize());
        }

        String contentType = image.getContentType();
        if (contentType == null) {
            throw new ValidationException("You cannot upload file %s to post %s without contentType.", imageName, postId);
        }

        List<String> availableImageTypes = ImageProcessingUtils.getAvailableImageTypes();
        if (availableImageTypes.stream().noneMatch(type -> type.equals(contentType))) {
            throw new ValidationException("You cannot upload file %s to post %s with %s contentType", imageName, postId, contentType);
        }
    }
}
