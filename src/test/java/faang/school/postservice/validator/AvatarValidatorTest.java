package faang.school.postservice.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AvatarValidatorTest {

    @InjectMocks
    private AvatarValidator avatarValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("test that saveAvatar() throws IllegalArgumentException when file too large")
    void testSaveAvatarThrowsException() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getSize()).thenReturn(6 * 1024 * 1024L); // Set file size to 6 MB

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> avatarValidator.validateFileSize(mockFile)
        );

        assertEquals("File size exceeds 5 MB", thrown.getMessage());
    }
}
