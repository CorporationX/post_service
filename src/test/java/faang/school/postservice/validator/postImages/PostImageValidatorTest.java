package faang.school.postservice.validator.postImages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostImageValidatorTest {

    @InjectMocks
    private PostImageValidator postImageValidator;

    @Mock
    private MultipartFile image;

    private static final long MAX_FILE_SIZE = 5242816;
    private static final long LARGE_FILE_SIZE = 5300000;
    private static final long DEFAULT_FILE_SIZE = 3000000;

    @Nested
    class ValidatorTests {
        @Test
        @DisplayName("Success if the size is equal to the allowed size")
        void whenImageSizeExceededAllowedSizeThenSuccess() {
            when(image.getSize()).thenReturn(MAX_FILE_SIZE);

            assertDoesNotThrow(() -> postImageValidator.checkImageSizeExceeded(image));
        }

        @Test
        @DisplayName("Success if the size is smaller than allowed")
        void whenImageSizeNotExceededThenSuccess() {
            when(image.getSize()).thenReturn(DEFAULT_FILE_SIZE);

            assertDoesNotThrow(() -> postImageValidator.checkImageSizeExceeded(image));
        }

        @Test
        @DisplayName("Error when exceeding the allowable size")
        void whenImageSizeExceededThenTrowException() {
            when(image.getSize()).thenReturn(LARGE_FILE_SIZE);

            assertThrows(MaxUploadSizeExceededException.class,
                    () -> postImageValidator.checkImageSizeExceeded(image));
        }

        @Test
        @DisplayName("Test list capacity")
        void whenListImagesSizeThenSuccess() {
            int allowedSize = 10;

            List<MultipartFile> images = new ArrayList<>(10){{
                add(image);add(image);add(image);add(image);add(image);
                add(image);add(image);add(image);add(image);add(image);
            }};

            assertEquals(allowedSize, images.size());

            images.add(image);
            assertTrue(images.size() > allowedSize);
        }
    }
}