package faang.school.postservice.validator;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.exception.ResourceLimitExceededException;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceServiceValidator {
    @Value("${spring.resources.file.max-file-size}")
    private int maxFileSize;

    @Value("${spring.resources.image.supported-image-types}")
    private Set<String> SUPPORTED_IMAGE_TYPES;

    @Value("${spring.post.max-image-quantity}")
    private int MaxQuantityImageInPost;

    public void validAddImages(List<MultipartFile> imageFiles, List<Resource> postResources) {
        imageFiles.forEach(imageFile -> {
            validateResourceSize(imageFile.getSize());
            checkIfFileAreImages(imageFile);
        });
        checkingThereEnoughSpaceInPostToImage(postResources.size(), imageFiles.size());
    }

    public void validAddImage(MultipartFile imageFile, List<Resource> postResources) {
        validateResourceSize(imageFile.getSize());
        checkIfFileAreImages(imageFile);
        checkingThereEnoughSpaceInPostToImage(postResources.size(), 1);
    }

    public void validateResourceSize(Long fileSize) {
        if (fileSize > maxFileSize) {
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
