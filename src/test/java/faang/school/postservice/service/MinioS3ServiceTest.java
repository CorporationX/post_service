package faang.school.postservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.exception.FileDeletionException;
import faang.school.postservice.exception.FileException;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinioS3ServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private MinioS3Service minioS3Service;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(minioS3Service, "bucketName", bucketName);
    }

    @Test
    void uploadFile_shouldUploadFileSuccessfully() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getOriginalFilename()).thenReturn("test.png");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);

        Resource resource = minioS3Service.uploadFile(multipartFile, "test-folder");

        verify(s3Client, times(1)).putObject(captor.capture());
        PutObjectRequest capturedRequest = captor.getValue();
        assertEquals(bucketName, capturedRequest.getBucketName());
        assertTrue(capturedRequest.getKey().startsWith("test-folder/"));
        assertNotNull(resource);
        assertEquals("test.png", resource.getName());
        assertEquals(1024L, resource.getSize());
        assertEquals("image/png", resource.getType());
    }

    @Test
    void uploadFile_shouldThrowFileExceptionOnIOException() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getInputStream()).thenThrow(new IOException("Mocked IO Exception"));

        FileException exception = assertThrows(FileException.class, () -> minioS3Service.uploadFile(multipartFile, "test-folder"));
        assertEquals("Error uploading file: Mocked IO Exception", exception.getMessage());
    }

    @Test
    void deleteFile_shouldDeleteFileSuccessfully() {
        String fileKey = "test-folder/test.png";

        minioS3Service.deleteFile(fileKey);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFile_shouldThrowFileDeletionExceptionOnFailure() {
        String fileKey = "test-folder/test.png";
        doThrow(new RuntimeException("Mocked delete failure")).when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        FileDeletionException exception = assertThrows(FileDeletionException.class, () -> minioS3Service.deleteFile(fileKey));
        assertEquals("Error while deleting file with key: " + fileKey, exception.getMessage());
    }
}
