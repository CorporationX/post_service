package faang.school.postservice.validator;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.exception.ResourceLimitExceededException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ResourceServiceValidatorTest {

    @InjectMocks
    private ResourceServiceValidator resourceServiceValidator;

    @Test
    @DisplayName("testValidateResourceSize with size exceeding limit")
    void testValidateResourceSize() {
        Long largeFileSize = 6 * 1024 * 1024L; // 6MB
        assertThrows(FileException.class, () -> {
            resourceServiceValidator.validateResourceSize(largeFileSize);
        });
    }

    @Test
    @DisplayName("testValidateResourceSize with size within limit")
    void testValidateResourceSizeWithinLimit() {
        Long smallFileSize = 4 * 1024 * 1024L; // 4MB
        resourceServiceValidator.validateResourceSize(smallFileSize);
    }

    @Test
    @DisplayName("testCheckIfFileAreImages with unsupported file type")
    void testCheckIfFileAreImagesUnsupportedType() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "some data".getBytes());
        assertThrows(FileException.class, () -> {
            resourceServiceValidator.checkIfFileAreImages(mockFile);
        });
    }

    @Test
    @DisplayName("testCheckIfFileAreImages with supported file type")
    void testCheckIfFileAreImagesSupportedType() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.png", "image/png", "some data".getBytes());
        resourceServiceValidator.checkIfFileAreImages(mockFile);
    }

    @Test
    @DisplayName("testCheckingThereEnoughSpaceInPostToImage exceeding max limit")
    void testCheckingThereEnoughSpaceInPostToImageExceedingLimit() {
        int currentImages = 8;
        int imagesToAdd = 3;

        ReflectionTestUtils.setField(resourceServiceValidator, "MaxQuantityImageInPost", 10);
        assertThrows(ResourceLimitExceededException.class, () -> {
            resourceServiceValidator.checkingThereEnoughSpaceInPostToImage(currentImages, imagesToAdd);
        });
    }

    @Test
    @DisplayName("testCheckingThereEnoughSpaceInPostToImage within limit")
    void testCheckingThereEnoughSpaceInPostToImageWithinLimit() {
        int currentImages = 5;
        int imagesToAdd = 2;

        ReflectionTestUtils.setField(resourceServiceValidator, "MaxQuantityImageInPost", 10);
        resourceServiceValidator.checkingThereEnoughSpaceInPostToImage(currentImages, imagesToAdd);
    }
}
