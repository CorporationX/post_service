package faang.school.postservice.validation.image;

import faang.school.postservice.config.file.FileProperties;
import faang.school.postservice.exception.file.FileTooLargeException;
import faang.school.postservice.exception.file.UnsupportedFileTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageValidatorTest {

    private static final String VALID_IMAGE_PATH = "test-images/kik.jpg";
    private static final String VALID_IMAGE_NAME = "kik.jpg";
    private static final String VALID_IMAGE_TYPE = "image/jpeg";
    private static final long MAX_FILE_SIZE = 10485760;

    @Spy
    private FileProperties fileProperties;

    @InjectMocks
    private ImageValidator imageValidator;

    private byte[] validBytes;

    @BeforeEach
    void setUp() throws IOException {
        ClassPathResource resource = new ClassPathResource(VALID_IMAGE_PATH);
        validBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
    }

    @Test
    void testValidateImageSuccessfully() {
        when(fileProperties.getMaxSize()).thenReturn(MAX_FILE_SIZE);

        MultipartFile file = new MockMultipartFile("file", VALID_IMAGE_NAME, VALID_IMAGE_TYPE, validBytes);
        assertDoesNotThrow(() -> imageValidator.validate(file));
    }

    @Test
    void testValidateImage_fileTooLarge() {
        when(fileProperties.getMaxSize()).thenReturn(MAX_FILE_SIZE);

        int largeFileSize = (int) (MAX_FILE_SIZE + 1);
        MultipartFile file = new MockMultipartFile("file", VALID_IMAGE_NAME, VALID_IMAGE_TYPE, new byte[largeFileSize]);
        assertThrows(FileTooLargeException.class, () -> imageValidator.validate(file));
    }

    @Test
    void testValidateImage_unsupportedMediaType() {
        when(fileProperties.getMaxSize()).thenReturn(MAX_FILE_SIZE);

        MultipartFile file = new MockMultipartFile("file", "image.jpg", "application/pdf", validBytes);
        assertThrows(UnsupportedFileTypeException.class, () -> imageValidator.validate(file));
    }

    @Test
    void testValidateImage_withoutExtension() {
        when(fileProperties.getMaxSize()).thenReturn(MAX_FILE_SIZE);

        MultipartFile file = new MockMultipartFile("file", "image", VALID_IMAGE_TYPE, validBytes);
        assertThrows(UnsupportedFileTypeException.class, () -> imageValidator.validate(file));
    }

    @Test
    void testValidateImage_withDotAtEnd() {
        when(fileProperties.getMaxSize()).thenReturn(MAX_FILE_SIZE);

        MultipartFile file = new MockMultipartFile("file", "image.", VALID_IMAGE_TYPE, validBytes);
        assertThrows(UnsupportedFileTypeException.class, () -> imageValidator.validate(file));
    }

    @Test
    void testValidateImage_nullFile() {
        assertThrows(UnsupportedFileTypeException.class, () -> imageValidator.validate(null));
    }

    @Test
    void testValidateImage_emptyFile() {
        MultipartFile file = new MockMultipartFile("file", "empty.jpg", VALID_IMAGE_TYPE, new byte[0]);
        assertThrows(UnsupportedFileTypeException.class, () -> imageValidator.validate(file));
    }
}
