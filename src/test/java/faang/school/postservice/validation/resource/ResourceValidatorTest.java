package faang.school.postservice.validation.resource;

import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ResourceValidatorTest {

    @InjectMocks
    ResourceValidator resourceValidator;

    @BeforeEach
    void init() throws NoSuchFieldException, IllegalAccessException {
        Field maxImageSize = resourceValidator.getClass().getDeclaredField("maxImageSize");
        Field maxVideoOrAudioSize = resourceValidator.getClass().getDeclaredField("maxVideoOrAudioSize");
        maxImageSize.setAccessible(true);
        maxVideoOrAudioSize.setAccessible(true);
        maxImageSize.set(resourceValidator, 5);
        maxVideoOrAudioSize.set(resourceValidator, 100);
    }

    @Test
    void validateImageFileSize_InvalidFileSize_ThrowsDataValidationException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", getSixMbFileSize());

        assertThrows(DataValidationException.class, () -> resourceValidator.validateImageFileSize(mockMultipartFile));
    }

    @Test
    void validateImageFileSize_ValidArgs_DoesNotThrowException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", getFiveMbFileSize());

        assertDoesNotThrow(() -> resourceValidator.validateImageFileSize(mockMultipartFile));
    }

    @Test
    void validateAudioOrVideoFileSize_InvalidFileSize_ThrowsDataValidationException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.mpeg", "video/mpeg", get101MbFileSize());

        assertThrows(DataValidationException.class, () -> resourceValidator.validateAudioOrVideoFileSize(mockMultipartFile));
    }

    @Test
    void validateAudioOrVideoFileSize_ValidArgs_DoesNotThrowException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.mpeg", "video/mpeg", getSixMbFileSize());

        assertDoesNotThrow(() -> resourceValidator.validateAudioOrVideoFileSize(mockMultipartFile));
    }

    @Test
    void validateTypeAudioOrVideo_InvalidContentType_ThrowsDataValidationException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", getSixMbFileSize());

        assertThrows(DataValidationException.class, () -> resourceValidator.validateTypeAudioOrVideo(mockMultipartFile));
    }

    @Test
    void validateTypeAudioOrVideo_ValidArgs_DoesNotThrowException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.mpeg", "video/mpeg", getSixMbFileSize());

        assertDoesNotThrow(() -> resourceValidator.validateTypeAudioOrVideo(mockMultipartFile));
    }

    private byte[] getSixMbFileSize() {
        int fileSize = 6 * 1024 * 1024;
        byte[] fileData = new byte[fileSize];
        Arrays.fill(fileData, (byte) 0);
        return fileData;
    }

    private byte[] getFiveMbFileSize() {
        int fileSize = 5 * 1024 * 1024;
        byte[] fileData = new byte[fileSize];
        Arrays.fill(fileData, (byte) 0);
        return fileData;
    }

    private byte[] get101MbFileSize() {
        int fileSize = 101 * 1024 * 1024;
        byte[] fileData = new byte[fileSize];
        Arrays.fill(fileData, (byte) 0);
        return fileData;
    }
}
