package faang.school.postservice.validator;

import faang.school.postservice.config.resource.ResourceProperties;
import faang.school.postservice.exception.FileValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceValidatorTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceProperties resourceProperties;

    @InjectMocks
    private ResourceValidator resourceValidator;

    private static final Long POST_ID = 1L;
    private static final Long FILE_SIZE = 1024L;
    private static final MultipartFile ONE_FILE = mock(MultipartFile.class);
    private static final List<MultipartFile> THREE_FILES = Arrays.asList(
            mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class)
    );
    private static final int THREE_FILE_FOR_ADD = 3;
    private static final int TWO_FILE_FOR_DELETE = 2;
    private static final int FIVE_FILE_FOR_DELETE = 5;

    @Test
    void validatePostExists_PostExistsNoException() {
        when(postRepository.existsByIdAndDeletedFalse(POST_ID)).thenReturn(true);

        assertDoesNotThrow(() -> resourceValidator.validatePostExists(POST_ID));
    }

    @Test
    void validatePostExists_ExceptionWhenPostNotFound() {
        when(postRepository.existsByIdAndDeletedFalse(POST_ID)).thenReturn(false);

        assertThrows(PostNotFoundException.class,
                () -> resourceValidator.validatePostExists(POST_ID));
    }

    @Test
    void validateFiles_ValidFilesNoException() {
        when(ONE_FILE.isEmpty()).thenReturn(false);
        when(ONE_FILE.getSize()).thenReturn(FILE_SIZE);
        when(ONE_FILE.getContentType()).thenReturn("image/jpeg");

        when(resourceProperties.getMaxFilesPerPost()).thenReturn(10);
        when(resourceProperties.getMaxFileSize()).thenReturn(10485760L);
        when(resourceProperties.getAllowedContentTypes()).thenReturn(List.of("image/jpeg"));

        assertDoesNotThrow(() -> resourceValidator.validateFiles(List.of(ONE_FILE)));
    }

    @Test
    void validateFiles_ThrowsExceptionWhenEmptyFile() {
        when(ONE_FILE.isEmpty()).thenReturn(true);
        when(ONE_FILE.getOriginalFilename()).thenReturn("empty.jpg");

        assertThrows(FileValidationException.class,
                () -> resourceValidator.validateFiles(List.of(ONE_FILE)));
    }

    @Test
    void validateFiles_ThrowsExceptionWhenFileTooLarge() {
        when(ONE_FILE.isEmpty()).thenReturn(false);
        when(ONE_FILE.getSize()).thenReturn(10485761L);
        when(resourceProperties.getMaxFileSize()).thenReturn(10485760L);
        when(ONE_FILE.getOriginalFilename()).thenReturn("large.jpg");

        assertEquals(10485760, resourceProperties.getMaxFileSize());


        assertThrows(FileValidationException.class,
                () -> resourceValidator.validateFiles(List.of(ONE_FILE)));
    }

    @Test
    void validateFiles_ThrowsExceptionWhenInvalidContentType() {
        when(ONE_FILE.isEmpty()).thenReturn(false);
        when(ONE_FILE.getSize()).thenReturn(10485760L);
        when(resourceProperties.getMaxFileSize()).thenReturn(10485760L);
        when(ONE_FILE.getContentType()).thenReturn("application/pdf");
        when(resourceProperties.getAllowedContentTypes()).thenReturn(List.of("image/jpeg", "image/png"));

        assertEquals(List.of("image/jpeg", "image/png"), resourceProperties.getAllowedContentTypes());
        assertEquals(10485760, resourceProperties.getMaxFileSize());
        assertThrows(FileValidationException.class,
                () -> resourceValidator.validateFiles(List.of(ONE_FILE)));
    }

    @Test
    void validateFiles_ThrowsExceptionWhenTooManyFiles() {
        when(resourceProperties.getMaxFilesPerPost()).thenReturn(2);

        assertThrows(FileValidationException.class,
                () -> resourceValidator.validateFiles(THREE_FILES));
    }

    @Test
    void validateFileLimit_NoExceptionWhenUnderLimit() {
        when(resourceRepository.countByPostId(POST_ID)).thenReturn(2L);
        when(resourceProperties.getMaxFilesPerPost()).thenReturn(10);

        assertDoesNotThrow(() -> resourceValidator.validateFileLimit(POST_ID, THREE_FILE_FOR_ADD));
    }

    @Test
    void validateFileLimit_ThrowsExceptionWhenOverLimit() {
        when(resourceRepository.countByPostId(POST_ID)).thenReturn(8L);
        when(resourceProperties.getMaxFilesPerPost()).thenReturn(10);

        assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateFileLimit(POST_ID, THREE_FILE_FOR_ADD));
    }

    @Test
    void validateFileLimitOnUpdate_NoExceptionWhenUnderLimitAfterDelete() {
        when(resourceRepository.countByPostId(POST_ID)).thenReturn(8L);
        when(resourceProperties.getMaxFilesPerPost()).thenReturn(10);

        assertDoesNotThrow(() ->
                resourceValidator.validateFileLimitOnUpdate(POST_ID, THREE_FILE_FOR_ADD, TWO_FILE_FOR_DELETE));
    }

    @Test
    void validateFileLimitOnUpdate_ThrowsExceptionWhenOverLimitAfterDelete() {
        when(resourceRepository.countByPostId(POST_ID)).thenReturn(8L);
        when(resourceProperties.getMaxFilesPerPost()).thenReturn(10);

        assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.validateFileLimitOnUpdate(POST_ID, FIVE_FILE_FOR_DELETE, TWO_FILE_FOR_DELETE));
    }
}
