package faang.school.postservice.validator.resource;

import faang.school.postservice.exception.MediaFileException;
import faang.school.postservice.model.resource.Resource;
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
public class ResourceValidator {
    @Value("${resources.file.max-file-size}")
    private int maxFileSize;

    @Value("${resources.image.supported-image-types}")
    private Set<String> supportedImageTypes;

    @Value("${post.max-images-per-post}")
    private int maxImagesPerPost;

    public void validateImages(List<MultipartFile> imageFiles, List<Resource> postResources) {
        imageFiles.forEach(imageFile -> {
            validateSize(imageFile);
            validateFileTypes(imageFile);
        });
        validateAvailableSpace(postResources.size(), imageFiles.size());
    }

    private void validateSize(MultipartFile imageFile) {
        if (imageFile.getSize() > maxFileSize) {
            throw new MediaFileException("File '" +  imageFile.getOriginalFilename() +
                    "' exceeds the size limit of 5 MB");
        }
    }

    private void validateFileTypes(MultipartFile imageFile) {
        if (!supportedImageTypes.contains(imageFile.getContentType())) {
            throw new MediaFileException(imageFile.getContentType() + " type is not supported");
        }
    }

    private void validateAvailableSpace(
            int numResourcesInPost, int numResourcesToAdd) {
        if (numResourcesInPost + numResourcesToAdd > maxImagesPerPost) {
            throw new MediaFileException(
                    String.format("You cannot attach more than %d images to a post." +
                                    " This post has %d images attached and you've tried to add %d more images.",
                            maxImagesPerPost, numResourcesInPost, numResourcesToAdd));
        }
    }
}
