package faang.school.postservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AmazonS3ServiceTest {
    @Mock
    private AmazonS3 s3Client;
    @InjectMocks
    private AmazonS3Service amazonS3Service;
    private MultipartFile fileMock;
    private final String key = "key";

    @BeforeEach
    void init () {
        fileMock = mock(MultipartFile.class);
    }

    @Test
    void testUploadFile () {
        String folderName = "folder";
        Resource resource = amazonS3Service.uploadFile(fileMock, folderName);

        verify(s3Client, times(1)).putObject(any());
        assertNotNull(resource);
    }

    @Test
    void testDeleteFile () {
        amazonS3Service.deleteFile(key);

        verify(s3Client, times(1)).deleteObject(any(), anyString());
    }

    @Test
    void testDownloadFile () {
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream inputStreamMock = mock(S3ObjectInputStream.class);
        when(s3Client.getObject(any(), anyString())).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStreamMock);

        amazonS3Service.downloadFile(key);

        verify(s3Client, times(1)).getObject(any(), anyString());
        verify(s3Object, times(1)).getObjectContent();
    }
}