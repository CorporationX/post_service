package faang.school.postservice.util.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import faang.school.postservice.service.s3.S3StorageService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class S3StorageServiceTest {
    @Mock
    private AmazonS3 amazonS3;

    private S3StorageService storageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storageService = new S3StorageService();

        ReflectionTestUtils.setField(storageService, "s3Client", amazonS3);
        ReflectionTestUtils.setField(storageService, "bucketName", "test-bucket");
    }

    @Test
    void testUploadFileInputStreamPathStyleSuccess() {
        String key = "some/key.png";
        byte[] content = new byte[]{1, 2, 3};
        long contentLength = content.length;
        String contentType = "image/png";
        InputStream is = new ByteArrayInputStream(content);

        when(amazonS3.putObject(
                anyString(),
                anyString(),
                any(InputStream.class),
                any(ObjectMetadata.class)
        )).thenReturn(new PutObjectResult());

        storageService.uploadFile(key, is, contentLength, contentType);

        ArgumentCaptor<String> bucketCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor    = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InputStream> isCaptor = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<ObjectMetadata> metaCaptor = ArgumentCaptor.forClass(ObjectMetadata.class);

        verify(amazonS3, times(1)).putObject(
                bucketCaptor.capture(),
                keyCaptor.capture(),
                isCaptor.capture(),
                metaCaptor.capture()
        );

        assertThat(bucketCaptor.getValue()).isEqualTo("test-bucket");
        assertThat(keyCaptor.getValue()).isEqualTo(key);

        ObjectMetadata metadata = metaCaptor.getValue();
        assertThat(metadata.getContentLength()).isEqualTo(contentLength);
        assertThat(metadata.getContentType()).isEqualTo(contentType);

        assertThat(isCaptor.getValue()).isNotNull();
    }

    @Test
    void testUploadFileInputStreamWhenAmazonThrowsExceptionPropagates() {
        String key = "bad/key.jpg";
        byte[] content = new byte[]{5, 6, 7};
        ByteArrayInputStream is = new ByteArrayInputStream(content);

        AmazonServiceException awsEx = new AmazonServiceException("failure");
        doThrow(awsEx)
                .when(amazonS3)
                .putObject(
                        anyString(),
                        anyString(),
                        any(InputStream.class),
                        any(ObjectMetadata.class)
                );

        assertThatThrownBy(() ->
                storageService.uploadFile(key, is, content.length, "image/jpeg")
        )
                .isSameAs(awsEx);
    }

    @Test
    void testUploadFileMultipartFileDelegatesToStreamMethod() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "file.gif",
                "image/gif",
                new byte[]{9, 8, 7}
        );

        when(amazonS3.putObject(
                anyString(),
                anyString(),
                any(ByteArrayInputStream.class),
                any(ObjectMetadata.class))
        ).thenReturn(new PutObjectResult());

        storageService.uploadFile("path/file.gif", multipartFile);

        ArgumentCaptor<ObjectMetadata> mdCaptor = ArgumentCaptor.forClass(ObjectMetadata.class);
        verify(amazonS3, times(1))
                .putObject(
                        eq("test-bucket"),
                        eq("path/file.gif"),
                        any(ByteArrayInputStream.class),
                        mdCaptor.capture()
                );

        ObjectMetadata metadata = mdCaptor.getValue();
        assertThat(metadata.getContentLength()).isEqualTo(3L);
        assertThat(metadata.getContentType()).isEqualTo("image/gif");
    }

    @Test
    void testDownloadFileWhenExistsReturnsBytes() throws IOException {
        String key = "downloads/pic.png";
        byte[] data = new byte[]{1, 2, 3, 4, 5};
        S3Object mockObject = mock(S3Object.class);
        S3ObjectInputStream mockStream = new S3ObjectInputStream(
                new ByteArrayInputStream(data),
                null
        );
        when(amazonS3.getObject("test-bucket", key)).thenReturn(mockObject);
        when(mockObject.getObjectContent()).thenReturn(mockStream);

        Optional<byte[]> result = storageService.downloadFile(key);

        assertThat(result).isPresent();
        assertThat(result.get()).containsExactly(data);
        verify(amazonS3).getObject("test-bucket", key);
    }

    @Test
    void testDownloadFileWhenAmazonThrowsReturnsEmpty() throws IOException {
        String key = "nonexistent.gif";
        when(amazonS3.getObject("test-bucket", key))
                .thenThrow(new AmazonServiceException("not found"));

        Optional<byte[]> result = storageService.downloadFile(key);
        assertThat(result).isEmpty();

        verify(amazonS3).getObject("test-bucket", key);
    }

    @Test
    void testDeleteFileSuccess() {
        String key = "to/delete.txt";
        doNothing().when(amazonS3).deleteObject("test-bucket", key);

        storageService.deleteFile(key);

        verify(amazonS3).deleteObject("test-bucket", key);
    }

    @Test
    void deleteFileWhenAmazonThrowsLogsButDoesNotPropagate() {
        String key = "bad/to/delete";
        AmazonServiceException awsEx = new AmazonServiceException("boom");
        doThrow(awsEx).when(amazonS3).deleteObject("test-bucket", key);

        storageService.deleteFile(key);

        verify(amazonS3).deleteObject("test-bucket", key);
    }

    @Test
    void testGeneratePresignedUrlReturnsUrlString() throws MalformedURLException {
        String key = "presign/me.png";
        URL fakeUrl = URI.create("https://mocked-s3/pic?signature=xyz").toURL();
        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(fakeUrl);

        String result = storageService.generatePresignedUrl(key);
        assertThat(result).isEqualTo(fakeUrl.toString());

        ArgumentCaptor<GeneratePresignedUrlRequest> captor =
                ArgumentCaptor.forClass(GeneratePresignedUrlRequest.class);
        verify(amazonS3).generatePresignedUrl(captor.capture());
        GeneratePresignedUrlRequest req = captor.getValue();

        assertThat(req.getBucketName()).isEqualTo("test-bucket");
        assertThat(req.getKey()).isEqualTo(key);
        assertThat(req.getMethod()).isEqualTo(HttpMethod.GET);

        Date now = new Date();
        long diffMillis = req.getExpiration().getTime() - now.getTime();
        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
        assertThat(diffMinutes).isBetween(59L, 61L);
    }

    @Test
    void testGeneratePresignedUrlWhenAmazonThrowsPropagates() {
        String key = "will/fail";
        AmazonServiceException awsEx = new AmazonServiceException("noAuth");
        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenThrow(awsEx);

        assertThatThrownBy(() -> storageService.generatePresignedUrl(key))
                .isSameAs(awsEx);
    }
}
