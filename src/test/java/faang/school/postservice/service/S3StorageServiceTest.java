package faang.school.postservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.exception.ImageProcessingException;
import faang.school.postservice.service.s3.S3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3StorageService s3StorageService;

    private final String bucketName = "test-bucket";
    private final String testKey = "test-key";
    private final BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3StorageService, "bucketName", bucketName);
    }

    @Nested
    class UploadToS3Tests {
        @Test
        @DisplayName("Успешная загрузка изображения")
        void givenValidImage_WhenUploadToS3_ThenSuccess() throws IOException {
            String contentType = "image/jpeg";

            s3StorageService.uploadToS3(testKey, testImage, contentType);

            verify(amazonS3).putObject(
                    eq(bucketName),
                    eq(testKey),
                    any(ByteArrayInputStream.class),
                    any(ObjectMetadata.class)
            );
        }

        @Test
        @DisplayName("Ошибка при загрузке невалидного изображения")
        void givenInvalidImage_WhenUploadToS3_ThenThrowIOException() {
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            String contentType = "image/jpeg";

            try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
                mockedImageIO.when(() -> ImageIO.write(any(BufferedImage.class), anyString(), any(OutputStream.class)))
                        .thenThrow(new IOException("Failed to write image"));

                assertThrows(IOException.class,
                        () -> s3StorageService.uploadToS3(testKey, image, contentType));
            }
        }
    }

    @Nested
    class DeleteFileTests {
        @Test
        @DisplayName("Успешное удаление файла")
        void givenExistingFile_WhenDeleteFile_ThenSuccess() {
            s3StorageService.deleteFile(testKey);

            verify(amazonS3).deleteObject(bucketName, testKey);
        }

        @Test
        @DisplayName("Удаление с null ключом")
        void givenFileWithNullKey_WhenDeleteFile_ThenDoNothing() {
            s3StorageService.deleteFile(null);

            verifyNoInteractions(amazonS3);
        }

        @Test
        @DisplayName("Удаление несуществующего файла (404)")
        void givenNonExistentFile_WhenDeleteFile_ThenSkipSilently() {
            AmazonS3Exception notFoundException = new AmazonS3Exception("Not found");
            notFoundException.setStatusCode(404);
            doThrow(notFoundException).when(amazonS3).deleteObject(bucketName, testKey);

            s3StorageService.deleteFile(testKey);

            verify(amazonS3).deleteObject(bucketName, testKey);
        }

        @Test
        @DisplayName("Ошибка при удалении файла (не 404)")
        void givenFileWithS3Error_WhenDeleteFile_ThenThrowImageProcessingException() {
            AmazonS3Exception s3Exception = new AmazonS3Exception("Access denied");
            s3Exception.setStatusCode(403);
            doThrow(s3Exception).when(amazonS3).deleteObject(bucketName, testKey);

            assertThrows(ImageProcessingException.class,
                    () -> s3StorageService.deleteFile(testKey));
        }
    }

    @Test
    @DisplayName("Проверка установки bucketName")
    void whenSetBucketName_ThenValueIsCorrect() {
        String expectedBucketName = "test-bucket-123";

        ReflectionTestUtils.setField(s3StorageService, "bucketName", expectedBucketName);

        String actualBucketName = (String) ReflectionTestUtils.getField(s3StorageService, "bucketName");
        assertEquals(expectedBucketName, actualBucketName);
    }
}