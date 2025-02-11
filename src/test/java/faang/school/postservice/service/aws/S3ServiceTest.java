package faang.school.postservice.service.aws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private S3AsyncClient s3AsyncClient;

    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private S3Service s3Service;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor;

    @Captor
    private ArgumentCaptor<DeleteObjectRequest> deleteObjectRequestCaptor;

    @Captor
    private ArgumentCaptor<GetObjectRequest> getObjectRequestCaptor;

    @BeforeEach
    void setUp() {
        Logger logger = LoggerFactory.getLogger(S3Service.class);
    }

    @Test
    void testUploadFileAsync() {
        String bucketName = "test-bucket";
        String key = "test-key";
        Map<String, String> metadata = Map.of("key1", "value1");
        byte[] fileBytes = "test-content".getBytes();

        PutObjectResponse putObjectResponse = PutObjectResponse.builder().build();
        CompletableFuture<PutObjectResponse> future = CompletableFuture.completedFuture(putObjectResponse);
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class))).thenReturn(future);

        CompletableFuture<PutObjectResponse> response = s3Service.uploadFileAsync(bucketName, key, metadata, fileBytes);

        assertNotNull(response);
        assertEquals(putObjectResponse, response.join());
        verify(s3AsyncClient).putObject(putObjectRequestCaptor.capture(), any(AsyncRequestBody.class));
        assertEquals(bucketName, putObjectRequestCaptor.getValue().bucket());
        assertEquals(key, putObjectRequestCaptor.getValue().key());
        assertEquals(metadata, putObjectRequestCaptor.getValue().metadata());
    }

    @Test
    void testDeleteFileAsync() {
        String bucketName = "test-bucket";
        String key = "test-key";

        DeleteObjectResponse deleteObjectResponse = DeleteObjectResponse.builder().build();
        CompletableFuture<DeleteObjectResponse> future = CompletableFuture.completedFuture(deleteObjectResponse);
        when(s3AsyncClient.deleteObject(any(DeleteObjectRequest.class))).thenReturn(future);

        CompletableFuture<Void> response = s3Service.deleteFileAsync(bucketName, key);

        assertNotNull(response);
        response.join();
        verify(s3AsyncClient).deleteObject(deleteObjectRequestCaptor.capture());
        assertEquals(bucketName, deleteObjectRequestCaptor.getValue().bucket());
        assertEquals(key, deleteObjectRequestCaptor.getValue().key());
    }

    @Test
    void testCreatePresignedGetUrl() throws MalformedURLException {
        String bucketName = "test-bucket";
        String keyName = "test-key";

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new java.net.URL("http://example.com"));
        when(presignedRequest.httpRequest()).thenReturn(mock(software.amazon.awssdk.http.SdkHttpRequest.class));
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);

        String url = s3Service.createPresignedGetUrl(bucketName, keyName);

        assertNotNull(url);
        assertEquals("http://example.com", url);
        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void testGetObjectBytes() {
        String bucketName = "test-bucket";
        String keyName = "test-key";

        GetObjectResponse getObjectResponse = GetObjectResponse.builder().build();
        byte[] content = "test-content".getBytes();
        ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(getObjectResponse, content);
        CompletableFuture<ResponseBytes<GetObjectResponse>> future = CompletableFuture.completedFuture(responseBytes);
        when(s3AsyncClient.getObject(any(GetObjectRequest.class), any(AsyncResponseTransformer.class))).thenReturn(future);

        byte[] result = s3Service.getObjectBytes(bucketName, keyName);

        assertNotNull(result);
        assertArrayEquals("test-content".getBytes(), result);
        verify(s3AsyncClient).getObject(getObjectRequestCaptor.capture(), any(AsyncResponseTransformer.class));
        assertEquals(bucketName, getObjectRequestCaptor.getValue().bucket());
        assertEquals(keyName, getObjectRequestCaptor.getValue().key());
    }
}