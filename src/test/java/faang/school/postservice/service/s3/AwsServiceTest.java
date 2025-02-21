package faang.school.postservice.service.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AwsServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private AwsService awsService;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String FILE_KEY = "test-file.txt";
    private byte[] mockData;

    @BeforeEach
    void setUp() {
         mockData = "data".getBytes();
    }

    @Test
    void testUploadFile_Success() {
        awsService.uploadFile(BUCKET_NAME, FILE_KEY, mockData);

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testDownloadFile_Success() {
        InputStream inputStream = new ByteArrayInputStream(mockData);
        ResponseInputStream<GetObjectResponse> mockResponseStream = new ResponseInputStream<>(GetObjectResponse.builder().build(), inputStream);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockResponseStream);

        byte[] actualBytes = awsService.downloadFile(BUCKET_NAME, FILE_KEY);

        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
        assertArrayEquals(mockData, actualBytes);
    }

    @Test
    void testDeleteFile() {
        awsService.deleteFile(BUCKET_NAME, FILE_KEY);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}
