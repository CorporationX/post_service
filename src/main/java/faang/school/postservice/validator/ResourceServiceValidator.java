package faang.school.postservice.validator;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.exception.ResourceLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceServiceValidator {
    private final static int MAX_FILE_SIZE = 5 * 1024 * 1024; //5mb
    private final static Set<String> SUPPORTED_IMAGE_TYPES = Set.of("image/png", "image/jpeg", "image/jpg");

    @Value("${spring.post.max-image-quantity}")
    private int MaxQuantityImageInPost;

    public void validateResourceSize(Long fileSize) {
        if (fileSize > MAX_FILE_SIZE) {
            throw new FileException("File size exceeds the 5MB limit");
        }
    }

    public void checkIfFileAreImages(MultipartFile imageFile) {
        if (!SUPPORTED_IMAGE_TYPES.contains(imageFile.getContentType())) {
            throw new FileException("File type " + imageFile.getContentType() + " not supported");
        }
    }

    public void checkingThereEnoughSpaceInPostToImage(
            int quantityImageInPost, int quantityAddingImage) {
        if (quantityImageInPost + quantityAddingImage > MaxQuantityImageInPost) {
            throw new ResourceLimitExceededException(
                    String.format("The post cannot contain more than %d resources." +
                                    "  Attempted to add: %d, Available space: %d",
                            quantityImageInPost, quantityAddingImage, MaxQuantityImageInPost - quantityImageInPost));
        }
    }
}
