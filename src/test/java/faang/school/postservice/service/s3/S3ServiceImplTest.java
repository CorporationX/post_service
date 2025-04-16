package faang.school.postservice.service.s3;

import faang.school.postservice.dto.resource.S3UploadDto;
import faang.school.postservice.exception.S3Exception;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.InvalidRequestException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class S3ServiceImplTest {

    @Mock
    private S3Client s3Client;
    @InjectMocks
    private S3ServiceImpl s3ServiceImpl;

    @Test
    public void testUploadResourceWithThrowException() {
        doThrow(InvalidRequestException.class)
                .when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertThrows(S3Exception.class,
                () -> s3ServiceImpl.uploadResource(
                        new S3UploadDto(
                                1L,
                                123L,
                                "filename.jpg",
                                "image/jpeg",
                                "bytes".getBytes()))
        );
    }

    @Test
    public void testUploadResource() {
        Resource actualResource = s3ServiceImpl.uploadResource(
                new S3UploadDto(
                        1L,
                        123L,
                        "filename.jpg",
                        "image/jpeg",
                        "bytes".getBytes())
        );

        verify(s3Client, times(1))
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertEquals(123L, actualResource.getSize());
        assertEquals("filename.jpg", actualResource.getName());
        assertEquals("image/jpeg", actualResource.getType());
        assertTrue(actualResource.getCreatedAt().isBefore(Instant.now()));
    }

    @Test
    public void testDownloadResourceWithThrowException() {
        doThrow(InvalidRequestException.class)
                .when(s3Client).getObject(any(GetObjectRequest.class));

        assertThrows(S3Exception.class, () -> s3ServiceImpl.downloadResource("some-key"));
    }

    @Test
    public void testDownloadResource() {
        try (InputStream ignored1 = s3ServiceImpl.downloadResource("some-key")) {
            verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
        } catch (Exception ignored) {
        }
    }


    @Test
    public void testDeleteResourceWithThrowException() {
        doThrow(InvalidRequestException.class)
                .when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(S3Exception.class, () -> s3ServiceImpl.deleteResource("some-key"));
    }

    @Test
    public void testDeleteResource() {
        s3ServiceImpl.deleteResource("some-key");

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}
