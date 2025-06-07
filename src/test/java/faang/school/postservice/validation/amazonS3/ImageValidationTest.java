package faang.school.postservice.validation.amazonS3;

import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageValidationTest {

    private final ImageValidation imageValidation = new ImageValidation();

    @Mock
    private MultipartFile mockFile;

    @Test
    void testCheckIsImageWhenFileIsImage() {
        when(mockFile.getContentType()).thenReturn("image/jpeg");

        assertDoesNotThrow(() -> imageValidation.checkIsImage(mockFile));
    }

    @Test
    void testCheckIsImageWhenFileNotImage() {
        when(mockFile.getContentType()).thenReturn("doc/pdf");

        assertThrows(DataValidationException.class,
                () -> imageValidation.checkIsImage(mockFile));
    }

    @Test
    void testCheckIsImageWhenFileIsNull() {
        assertThrows(DataValidationException.class,
                () -> imageValidation.checkIsImage(null));
    }

    @Test
    void testCheckIsImageWhenContentIsNull() {
        when(mockFile.getContentType()).thenReturn(null);

        assertThrows(DataValidationException.class,
                () -> imageValidation.checkIsImage(mockFile));
    }
}