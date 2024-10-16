package faang.school.postservice.validator.resource;

import faang.school.postservice.exception.MediaFileException;
import faang.school.postservice.model.entity.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceValidatorTest {

    @Mock
    private MultipartFile imageFile;

    @InjectMocks
    private ResourceValidator resourceValidator;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(resourceValidator, "maxFileSize", 5 * 1024 * 1024);
        ReflectionTestUtils.setField(resourceValidator, "supportedImageTypes",
                Set.of("image/jpeg", "image/png", "image/jpg"));
        ReflectionTestUtils.setField(resourceValidator, "maxImagesPerPost", 10);
    }

    @Test
    @DisplayName("Should validate images successfully")
    public void testValidateImages_Success() {
        when(imageFile.getSize()).thenReturn(2 * 1024 * 1024L);
        when(imageFile.getContentType()).thenReturn("image/jpeg");

        List<MultipartFile> imageFiles = List.of(imageFile);
        List<Resource> postResources = List.of();
        assertDoesNotThrow(() -> resourceValidator.validateImages(imageFiles, postResources));
    }

    @Test
    @DisplayName("Should throw exception when file exceeds size limit")
    public void testValidateImages_FileExceedsSizeLimit() {
        when(imageFile.getSize()).thenReturn(10 * 1024 * 1024L);
        when(imageFile.getOriginalFilename()).thenReturn("large_image.jpg");

        List<MultipartFile> imageFiles = List.of(imageFile);
        MediaFileException exception = assertThrows(MediaFileException.class,
                () -> resourceValidator.validateImages(imageFiles, List.of()));

        assertEquals("File 'large_image.jpg' exceeds the size limit of 5 MB", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for unsupported file type")
    public void testValidateImages_UnsupportedFileType() {
        when(imageFile.getSize()).thenReturn(2 * 1024 * 1024L);
        when(imageFile.getContentType()).thenReturn("image/gif");

        List<MultipartFile> imageFiles = List.of(imageFile);

        MediaFileException exception = assertThrows(MediaFileException.class,
                () -> resourceValidator.validateImages(imageFiles, List.of()));

        assertEquals("image/gif type is not supported", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when too many images in post")
    public void testValidateImages_TooManyImagesInPost() {
        when(imageFile.getSize()).thenReturn(2 * 1024 * 1024L);
        when(imageFile.getContentType()).thenReturn("image/jpeg");

        List<Resource> postResources = List.of(
                new Resource(), new Resource(), new Resource(),
                new Resource(), new Resource(), new Resource(),
                new Resource(), new Resource(), new Resource());

        List<MultipartFile> imageFiles = List.of(imageFile, imageFile);

        MediaFileException exception = assertThrows(MediaFileException.class,
                () -> resourceValidator.validateImages(imageFiles, postResources));

        assertTrue(exception.getMessage().contains("You cannot attach more than 10 images to a post."));
    }
}
