package faang.school.postservice.service.s3;

import faang.school.postservice.exception.FileDownloadFailedException;
import faang.school.postservice.exception.FileUploadFailedException;
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
import java.time.LocalDateTime;

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
        ReflectionTestUtils.setField(s3Service, "bucketName", BUCKET_NAME);
    }

    @Test
    void uploadFile_shouldUploadSuccessfully_whenFileIsValid() throws IOException {
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
        RuntimeException exception = assertThrows(FileUploadFailedException.class, () -> {
            s3Service.uploadFile(mockFile, FOLDER_NAME);
        });
        assertEquals("Failed to upload file", exception.getMessage());
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

        InputStream resultStream = s3Service.getFileAsInputStream(resource);

        assertNotNull(resultStream);
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

        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(new FileDownloadFailedException("S3 SDK Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(FileDownloadFailedException.class, () -> {
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

    @Test
    void testUploadBytesAsResource_CallsS3PutObjectAndReturnsResource() {
        byte[] data = new byte[] {1, 2, 3, 4, 5};
        String key = "folder/subfolder/file.bin";
        String contentType = "application/octet-stream";

        PutObjectResponse fakeResponse = PutObjectResponse.builder().build();
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(fakeResponse);

        LocalDateTime beforeCall = LocalDateTime.now();

        Resource result = s3Service.uploadBytesAsResource(data, key, contentType);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), bodyCaptor.capture());

        PutObjectRequest passedRequest = requestCaptor.getValue();
        assertEquals("test-bucket", passedRequest.bucket(), "Bucket name должен совпадать с bucketName");
        assertEquals(key, passedRequest.key(), "Key должен совпадать");
        assertEquals(contentType, passedRequest.contentType(), "ContentType должен совпадать");
        assertEquals((Long) ((long) data.length), passedRequest.contentLength(), "ContentLength должен совпадать");

        RequestBody passedBody = bodyCaptor.getValue();
        assertNotNull(passedBody, "RequestBody не должен быть null");

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(key, result.getKey(), "Resource.key должен совпадать");
        assertEquals((Long) ((long) data.length), result.getSize(), "Resource.size должен совпадать");
        assertEquals(key, result.getName(), "Resource.name по коду установлен равным key");
        assertEquals(contentType, result.getType(), "Resource.type должен совпадать");

        assertNotNull(result.getCreatedAt(), "createdAt не должен быть null");
        LocalDateTime afterCall = LocalDateTime.now();
        assertFalse(result.getCreatedAt().isBefore(beforeCall),
                "createdAt должен быть не раньше времени до вызова");
        assertFalse(result.getCreatedAt().isAfter(afterCall.plusSeconds(1)),
                "createdAt должен быть не позже текущего времени");
    }

    @Test
    void testUploadBytesAsResource_S3ClientThrowsException_Propagates() {
        byte[] data = new byte[]{9,8,7};
        String key = "some/key";
        String contentType = "image/png";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("S3 error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            s3Service.uploadBytesAsResource(data, key, contentType);
        });
        assertTrue(ex.getMessage().contains("S3 error"));

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}