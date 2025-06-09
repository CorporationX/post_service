package faang.school.postservice.service.s3;

import faang.school.postservice.exception.file.FileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @InjectMocks
    private S3Service s3Service;

    @Mock
    private S3Client s3Client;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", "test-bucket");
    }

    @Test
    void download_shouldReturnStream() throws IOException {
        String key = "file.jpg";
        byte[] bytes = new byte[]{1, 2, 3};
        InputStream inputStream = new ByteArrayInputStream(bytes);
        GetObjectResponse response = GetObjectResponse.builder().build();

        ResponseInputStream<GetObjectResponse> responseInputStream =
                new ResponseInputStream<>(response, AbortableInputStream.create(inputStream));

        GetObjectRequest expectedRequest = GetObjectRequest.builder()
                .bucket("test-bucket")
                .key(key)
                .build();

        when(s3Client.getObject(eq(expectedRequest))).thenReturn(responseInputStream);

        InputStream result = s3Service.download(key);

        assertNotNull(result);
        assertEquals(bytes.length, result.readAllBytes().length);
    }


    @Test
    void download_shouldThrowFileNotFoundException_whenKeyMissing() {
        String key = "missing.jpg";

        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("not found").build());

        assertThrows(FileNotFoundException.class, () -> s3Service.download(key));
    }

    @Test
    void delete_shouldThrowFileNotFoundException_whenKeyMissing() {
        String key = "missing.jpg";

        doThrow(NoSuchKeyException.builder().message("not found").build())
                .when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(FileNotFoundException.class, () -> s3Service.delete(key));
    }

    @Test
    void getContentType_shouldReturnCorrectType() {
        String key = "image.png";
        String expectedType = "image/png";

        HeadObjectResponse response = HeadObjectResponse.builder()
                .contentType(expectedType)
                .build();

        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(response);

        String actual = s3Service.getContentType(key);

        assertEquals(expectedType, actual);
    }
}
