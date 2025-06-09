package faang.school.postservice.service.s3;

import faang.school.postservice.config.s3.S3Properties;
import faang.school.postservice.exception.file.FileDownloadException;
import faang.school.postservice.exception.file.FileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock private S3Client s3Client;
    @Mock private S3Properties s3Properties;
    @InjectMocks private S3Service s3Service;

    private final String bucket = "test-bucket";
    private final String fileKey = "images/image1.jpg";

    @BeforeEach
    void setUp() {
        when(s3Properties.getBucket()).thenReturn(bucket);
    }

    @Test
    void download_shouldReturnStream_whenFileExists() {
        GetObjectRequest expectedRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

        GetObjectResponse getObjectResponse = GetObjectResponse.builder().build();
        InputStream innerStream = new ByteArrayInputStream("test data".getBytes());

        ResponseInputStream<GetObjectResponse> responseStream =
                new ResponseInputStream<>(getObjectResponse, AbortableInputStream.create(innerStream));

        when(s3Client.getObject(eq(expectedRequest))).thenReturn(responseStream);

        InputStream result = s3Service.download(fileKey);

        assertNotNull(result);
        assertEquals("test data", new BufferedReader(new InputStreamReader(result))
                .lines().collect(Collectors.joining()));
    }

    @Test
    void download_shouldThrowFileNotFound_whenNoSuchKey() {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

        when(s3Client.getObject(eq(request))).thenThrow(NoSuchKeyException.builder().message("not found").build());

        assertThrows(FileNotFoundException.class, () -> s3Service.download(fileKey));
    }

    @Test
    void download_shouldThrowFileDownloadException_onGenericFailure() {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

        when(s3Client.getObject(eq(request))).thenThrow(SdkClientException.builder().message("AWS error").build());

        assertThrows(FileDownloadException.class, () -> s3Service.download(fileKey));
    }

    @Test
    void delete_shouldCallDeleteObject_whenFileExists() {
        DeleteObjectRequest expectedRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

        s3Service.delete(fileKey);

        verify(s3Client).deleteObject(eq(expectedRequest));
    }

    @Test
    void delete_shouldThrowFileNotFound_whenNoSuchKey() {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

        doThrow(NoSuchKeyException.builder().message("missing").build())
                .when(s3Client).deleteObject(eq(request));

        assertThrows(FileNotFoundException.class, () -> s3Service.delete(fileKey));
    }

    @Test
    void delete_shouldThrowIllegalState_whenGenericFailure() {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

        doThrow(RuntimeException.class).when(s3Client).deleteObject(eq(request));

        assertThrows(IllegalStateException.class, () -> s3Service.delete(fileKey));
    }
}
