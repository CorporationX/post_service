package faang.school.postservice.service.amazonS3;

import faang.school.postservice.dto.comment.CommentResponseImageDto;
import faang.school.postservice.exception.FileCorruptedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    private static final String TEST_KEY = "key";
    private static final String TEST_FILE_NAME = "testFileName";
    private static final String TEST_FOLDER = "testFolder";
    private static final String TEST_BUCKET = "testBucket";
    private static final String TEST_FILE_CONTENT = "image/jag";
    private static final long TEST_FILE_SIZE = 100;


    @Mock
    private S3Client s3Client;

    @Mock
    MultipartFile originalFile;

    @InjectMocks
    private S3Service s3Service;

    @Captor
    ArgumentCaptor<Consumer<DeleteObjectRequest.Builder>> requestCaptor;

    @Test
    void testUploadFileWhenUploaded() throws Exception {
        when(originalFile.getOriginalFilename()).thenReturn(TEST_FILE_NAME);
        when(originalFile.getSize()).thenReturn(TEST_FILE_SIZE);
        when(originalFile.getContentType()).thenReturn(TEST_FILE_CONTENT);
        when(originalFile.getInputStream()).thenReturn(InputStream.nullInputStream());

        ArgumentCaptor<PutObjectRequest> captorPut = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> captorBody = ArgumentCaptor.forClass(RequestBody.class);

        String resultKey = s3Service.uploadFile(TEST_FOLDER, originalFile);

        assertTrue(resultKey.startsWith(TEST_FOLDER + "/" + TEST_FILE_NAME + "-"));
        verify(s3Client).putObject(captorPut.capture(), captorBody.capture());
    }

    @Test
    void testUploadFileWhenS3ClientUnavailable() throws Exception {
        when(originalFile.getOriginalFilename()).thenReturn(TEST_FILE_NAME);
        when(originalFile.getSize()).thenReturn(TEST_FILE_SIZE);
        when(originalFile.getContentType()).thenReturn(TEST_FILE_CONTENT);
        when(originalFile.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(AwsServiceException.class);

        ArgumentCaptor<PutObjectRequest> captorPut = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> captorBody = ArgumentCaptor.forClass(RequestBody.class);

        assertThrows(AwsServiceException.class,
                () -> s3Service.uploadFile(TEST_FOLDER, originalFile));
        verify(s3Client).putObject(captorPut.capture(), captorBody.capture());
    }

    @Test
    void testUploadFileWhenFileCorrupted() throws Exception {
        when(originalFile.getOriginalFilename()).thenReturn(TEST_FILE_NAME);
        when(originalFile.getSize()).thenReturn(TEST_FILE_SIZE);
        when(originalFile.getContentType()).thenReturn(TEST_FILE_CONTENT);
        when(originalFile.getInputStream()).thenThrow(IOException.class);

        assertThrows(FileCorruptedException.class,
                () -> s3Service.uploadFile(TEST_FOLDER, originalFile));
    }

    @Test
    void testDeleteFileWhenNotDeleted() {
        ReflectionTestUtils.setField(s3Service, "bucketName", TEST_BUCKET);

        when(s3Client.deleteObject(requestCaptor.capture())).thenReturn(DeleteObjectResponse.builder().build());

        assertDoesNotThrow(() -> s3Service.deleteFile(TEST_KEY));
        verify(s3Client).deleteObject(requestCaptor.capture());
    }

    @Test
    void testDeleteFileWhenDeleted() {
        ReflectionTestUtils.setField(s3Service, "bucketName", TEST_BUCKET);

        when(s3Client.deleteObject(requestCaptor.capture())).thenThrow(AwsServiceException.class);

        assertThrows(AwsServiceException.class, () -> s3Service.deleteFile(TEST_KEY));
        verify(s3Client).deleteObject(requestCaptor.capture());
    }

    @Test
    void testDownloadFileWhenSuccessful() {
        GetObjectResponse response = GetObjectResponse.builder()
                .contentLength(TEST_FILE_SIZE)
                .contentType(TEST_FILE_CONTENT)
                .metadata(Map.of("file-name", TEST_FILE_NAME))
                .build();

        ResponseInputStream<GetObjectResponse> stream = new ResponseInputStream<>(
                response,
                new ByteArrayInputStream(new byte[0])
        );

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);

        CommentResponseImageDto result = s3Service.downloadFile(TEST_KEY);

        assertEquals(TEST_FILE_SIZE, result.getContentLength());
        assertEquals(TEST_FILE_NAME, result.getFileName());
        assertEquals(TEST_FILE_CONTENT, result.getContentType());
    }

    @Test
    void testDownloadFileWhenMetadataMissed() {
        GetObjectResponse response = GetObjectResponse.builder()
                .contentLength(TEST_FILE_SIZE)
                .contentType(TEST_FILE_CONTENT)
                .build();

        ResponseInputStream<GetObjectResponse> stream = new ResponseInputStream<>(
                response,
                new ByteArrayInputStream(new byte[0])
        );

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);

        assertThrows(FileCorruptedException.class,
                () -> s3Service.downloadFile(TEST_KEY));
    }

    @Test
    void testDownloadFileWhenNotSuccessful() {
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(AwsServiceException.class);

        assertThrows(AwsServiceException.class,
                () -> s3Service.downloadFile(TEST_KEY));
    }
}