package faang.school.postservice.service.amazons3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.postservice.service.amazons3.processing.ImageProcessingService;
import faang.school.postservice.validator.file.FileValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Amazons3ServiceImplTest {
    @Mock
    AmazonS3 s3Client;

    @Mock
    FileValidator fileValidator;

    @Mock
    ImageProcessingService imageProcessingService;

    @InjectMocks
    Amazons3ServiceImpl amazons3Service;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Test
    void testUploadFile_WhenBucketDoesNotExist() throws IOException {
        // Arrange
        String key = "test-key";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.png", "image/png", "test data".getBytes()
        );

        when(s3Client.doesBucketExistV2(bucketName)).thenReturn(false);
        when(fileValidator.contentIsImage(mockFile)).thenReturn(true);
        when(imageProcessingService.optimizeImage(any(InputStream.class)))
                .thenReturn(new ByteArrayInputStream("optimized data".getBytes()));
        when(imageProcessingService.toByteArray(any(InputStream.class))).thenReturn("optimized data".getBytes());

        amazons3Service.uploadFile(mockFile, key);

        verify(s3Client).createBucket(bucketName);
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture());
        assertEquals(bucketName, captor.getValue().getBucketName());
        assertEquals(key, captor.getValue().getKey());
    }

    @Test
    void testGetFile() {
        String fileId = "test-file-id";
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(
                new ByteArrayInputStream("file content".getBytes()), null
        );

        when(s3Client.getObject(bucketName, fileId)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);

        InputStream result = amazons3Service.getFile(fileId);

        verify(s3Client).getObject(bucketName, fileId);
        assertEquals(s3ObjectInputStream, result);
    }

    @Test
    void testDeleteFile() {
        String key = "test-key";
        amazons3Service.deleteFile(key);
        verify(s3Client).deleteObject(bucketName, key);
    }
}