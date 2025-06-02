package faang.school.postservice.service.s3;

import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private S3Client s3Client;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private S3Service s3Service;

    private final String BUCKET_NAME = "test-bucket";
    private final String FOLDER_NAME = "test-folder";

    @BeforeEach
    void setUp() {
        // Устанавливаем значение для поля, аннотированного @Value
        ReflectionTestUtils.setField(s3Service, "bucketName", BUCKET_NAME);
    }

    @Test
    void uploadFile_shouldUploadSuccessfully_whenFileIsValid() throws IOException {
        // Arrange
        String originalFilename = "image.jpg";
        String contentType = "image/jpeg";
        long fileSize = 1024L;
        byte[] content = new byte[(int) fileSize];
        InputStream inputStream = new ByteArrayInputStream(content);

        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getSize()).thenReturn(fileSize);
        when(mockFile.getInputStream()).thenReturn(inputStream);

        // Для s3Client.putObject, который возвращает PutObjectResponse
        PutObjectResponse mockPutResponse = PutObjectResponse.builder().build();
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(mockPutResponse);

        // Act
        Resource result = s3Service.uploadFile(mockFile, FOLDER_NAME);

        // Assert
        assertNotNull(result);
        assertTrue(result.getKey().startsWith(FOLDER_NAME + "/"));
        assertTrue(result.getKey().endsWith(".jpg"));
        assertEquals(originalFilename, result.getName());
        assertEquals(contentType, result.getType());
        assertEquals(fileSize, result.getSize());
        assertNotNull(result.getCreatedAt());

        ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(putObjectRequestCaptor.capture(), any(RequestBody.class));
        PutObjectRequest capturedRequest = putObjectRequestCaptor.getValue();
        assertEquals(BUCKET_NAME, capturedRequest.bucket());
        assertEquals(contentType, capturedRequest.contentType());
        assertEquals(fileSize, capturedRequest.contentLength());
    }

    @Test
    void uploadFile_shouldThrowRuntimeException_whenIoExceptionOccurs() throws IOException {
        // Arrange
        when(mockFile.getOriginalFilename()).thenReturn("error.txt");
        when(mockFile.getSize()).thenReturn(100L);
        when(mockFile.getInputStream()).thenThrow(new IOException("Test IO Exception"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            s3Service.uploadFile(mockFile, FOLDER_NAME);
        });
        assertEquals("Failed to upload file", exception.getMessage());
        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    void getFileAsInputStream_shouldReturnInputStream_whenFileExists() {
        // Arrange
        String fileKey = "test-folder/some-file.txt";
        Resource resource = Resource.builder().key(fileKey).build();
        InputStream mockInputStream = new ByteArrayInputStream("test content".getBytes());
        GetObjectResponse mockGetObjectResponse = GetObjectResponse.builder().build();
        ResponseInputStream<GetObjectResponse> mockResponseStream = new ResponseInputStream<>(mockGetObjectResponse, mockInputStream);

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockResponseStream);

        // Act
        InputStream resultStream = s3Service.getFileAsInputStream(resource);

        // Assert
        assertNotNull(resultStream);
        // Можно добавить проверку содержимого потока, если это важно
        // assertEquals("test content", new String(resultStream.readAllBytes()));

        ArgumentCaptor<GetObjectRequest> getObjectRequestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(getObjectRequestCaptor.capture());
        GetObjectRequest capturedRequest = getObjectRequestCaptor.getValue();
        assertEquals(BUCKET_NAME, capturedRequest.bucket());
        assertEquals(fileKey, capturedRequest.key());
    }

    @Test
    void getFileAsInputStream_shouldThrowRuntimeException_whenS3ClientFails() {
        // Arrange
        String fileKey = "test-folder/error-file.txt";
        Resource resource = Resource.builder().key(fileKey).build();

        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(new RuntimeException("S3 SDK Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            s3Service.getFileAsInputStream(resource);
        });
        assertEquals("Failed to download file", exception.getMessage());
    }

    @Test
    void deleteFile_shouldCallS3ClientDeleteObject() {
        // Arrange
        String fileKey = "test-folder/to-delete.jpg";

        // Act
        s3Service.deleteFile(fileKey);

        // Assert
        ArgumentCaptor<DeleteObjectRequest> deleteObjectRequestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(deleteObjectRequestCaptor.capture());
        DeleteObjectRequest capturedRequest = deleteObjectRequestCaptor.getValue();
        assertEquals(BUCKET_NAME, capturedRequest.bucket());
        assertEquals(fileKey, capturedRequest.key());
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}