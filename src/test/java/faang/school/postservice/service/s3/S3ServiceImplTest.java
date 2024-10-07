package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.dto.resource.ResourceObjectResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {
    private static final String BUCKET_NAME = "testBucket";
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final String FILE_KEY = "fileKeyTest";

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3ServiceImpl s3Service;

    private byte[] fileContent;

    @BeforeEach
    void setUp() {
        s3Service.setBucketName(BUCKET_NAME);
        fileContent = new byte[5];
    }

    @Test
    @DisplayName("Upload file to S3")
    void s3ServiceTest_uploadFile() {
        s3Service.uploadFile(fileContent, CONTENT_TYPE, FILE_KEY);

        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("Upload empty file to S3")
    void s3ServiceTest_uploadEmptyFile() {
        fileContent = new byte[0];

        assertThrows(IllegalArgumentException.class, () -> s3Service.uploadFile(fileContent, CONTENT_TYPE, FILE_KEY));
        verify(s3Client, times(0)).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("Upload file without Content Type")
    void s3ServiceTest_uploadFileWithoutContentType() {
        assertThrows(NullPointerException.class, () -> s3Service.uploadFile(fileContent, null, FILE_KEY));
        verify(s3Client, times(0)).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("Upload file without file key")
    void s3ServiceTest_uploadFileWithoutFileKey() {
        assertThrows(NullPointerException.class, () -> s3Service.uploadFile(fileContent, CONTENT_TYPE, null));
        verify(s3Client, times(0)).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("Delete file from S3")
    void s3ServiceTest_deleteFile() {
        s3Service.deleteFile(FILE_KEY);

        verify(s3Client).deleteObject(BUCKET_NAME, FILE_KEY);
    }

    @Test
    @DisplayName("Delete file from S3 without file key")
    void s3ServiceTest_deleteFileWithoutFileKey() {
        assertThrows(NullPointerException.class, () -> s3Service.deleteFile(null));
        verify(s3Client, times(0)).deleteObject(any(), any());
    }

    @Test
    @DisplayName("Download file from S3")
    void s3ServiceTest_downloadFile() {
        ResourceObjectResponse resourceObjectResponse = initResourceObjectResponse(
                new ByteArrayInputStream(fileContent), CONTENT_TYPE, fileContent.length);
        S3Object object = initObject(new ByteArrayInputStream(fileContent), CONTENT_TYPE, fileContent.length);
        when(s3Client.getObject(BUCKET_NAME, FILE_KEY)).thenReturn(object);

        var response = s3Service.downloadFile(FILE_KEY);

        try {
            List<Byte> result = getInputStreamContent(response.content());
            List<Byte> expected = getInputStreamContent(resourceObjectResponse.content());
            assertEquals(expected, result);
            verify(s3Client).getObject(BUCKET_NAME, FILE_KEY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Download file from S3 without file key")
    void s3ServiceTest_downloadFileWithoutFileKey() {
        assertThrows(NullPointerException.class, () -> s3Service.downloadFile(null));
        verify(s3Client, times(0)).getObject(any());
    }

    private ResourceObjectResponse initResourceObjectResponse(InputStream content, String contentType,
                                                              int contentLength) {
        return ResourceObjectResponse.builder()
                .content(content)
                .contentType(contentType)
                .contentLength(contentLength)
                .build();
    }

    private S3Object initObject(InputStream content, String contentType, int contentLength) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.setContentLength(contentLength);
        S3Object object = new S3Object();
        object.setObjectContent(content);
        object.setObjectMetadata(objectMetadata);
        return object;
    }

    private List<Byte> getInputStreamContent(InputStream inputStream) throws IOException {
        List<Byte> content = new ArrayList<>();
        for (byte b : inputStream.readAllBytes()) {
            content.add(b);
        }
        return content;
    }
}
