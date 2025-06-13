package faang.school.postservice.validation.image;

import faang.school.postservice.exception.file.FileTooLargeException;
import faang.school.postservice.exception.file.UnsupportedFileTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
    "file.upload.max-size=10485760",
    "spring.liquibase.enabled=false"
})
class ImageValidatorTest {

    private static final String VALID_IMAGE_PATH = "test-images/kik.jpg";
    private static final String VALID_IMAGE_NAME = "kik.jpg";
    private static final String VALID_IMAGE_TYPE = "image/jpeg";

    @Autowired
    private ImageValidator imageValidator;

    private byte[] validBytes;

    @BeforeEach
    void setUp() throws IOException {
        ClassPathResource resource = new ClassPathResource(VALID_IMAGE_PATH);
        validBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
    }

    @Test
    void testValidateImageSuccessfully() {
        MultipartFile file = new MockMultipartFile("file", VALID_IMAGE_NAME, VALID_IMAGE_TYPE, validBytes);
        assertDoesNotThrow(() -> imageValidator.validate(file));
    }

    @Test
    void testValidateImage_fileTooLarge() {
        MultipartFile file = new MockMultipartFile("file", VALID_IMAGE_NAME, VALID_IMAGE_TYPE, new byte[10485762]);
        assertThrows(FileTooLargeException.class, () -> imageValidator.validate(file));
    }

    @Test
    void testValidateImage_unsupportedMediaType() {
        MultipartFile file = new MockMultipartFile("file", "image.jpg", "application/pdf", validBytes);
        assertThrows(UnsupportedFileTypeException.class, () -> imageValidator.validate(file));
    }

    @Test
    void testValidateImage_withoutExtension() {
        MultipartFile file = new MockMultipartFile("file", "image", VALID_IMAGE_TYPE, validBytes);
        assertThrows(UnsupportedFileTypeException.class, () -> imageValidator.validate(file));
    }

    @Test
    void testValidateImage_withDotAtEnd() {
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
