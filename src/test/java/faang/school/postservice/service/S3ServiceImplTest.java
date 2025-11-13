package faang.school.postservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.exception.S3OperationException;
import faang.school.postservice.service.s3.S3ServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceImplTest {

    @Mock
    private AmazonS3 amazonS3;

    private S3ServiceImpl s3Service;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String KEY = "file123.jpg";

    @BeforeEach
    void setUp() throws Exception {
        s3Service = new S3ServiceImpl(amazonS3);

        Field bucketNameField = S3ServiceImpl.class.getDeclaredField("bucketName");
        bucketNameField.setAccessible(true);
        bucketNameField.set(s3Service, BUCKET_NAME);

        Field bucketCheckedField = S3ServiceImpl.class.getDeclaredField("bucketChecked");
        bucketCheckedField.setAccessible(true);
        AtomicBoolean bucketChecked = (AtomicBoolean) bucketCheckedField.get(s3Service);
        bucketChecked.set(false);
    }

    private MultipartFile mockValidMultipartFile() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("content".getBytes()));
        return file;
    }

    @Test
    void uploadFile_Success() throws IOException {
        when(amazonS3.doesBucketExistV2(BUCKET_NAME)).thenReturn(true);
        MultipartFile file = mockValidMultipartFile();

        String result = s3Service.uploadFile(file);

        verify(amazonS3).putObject(any(PutObjectRequest.class));
        assertThat(result).matches("[a-f0-9-]+\\.jpg");
    }

    @Test
    void uploadFile_ThrowsS3OperationExceptionWhenOnError() throws IOException {
        when(amazonS3.doesBucketExistV2(BUCKET_NAME)).thenReturn(true);
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getInputStream()).thenThrow(IOException.class);

        assertThrows(S3OperationException.class, () -> s3Service.uploadFile(file));
    }

    @Test
    void uploadFile_CreatesBucketIfNotExists() throws Exception {
        when(amazonS3.doesBucketExistV2(BUCKET_NAME)).thenReturn(false);
        MultipartFile file = mockValidMultipartFile();

        s3Service.uploadFile(file);

        verify(amazonS3).createBucket(BUCKET_NAME);
    }

    @Test
    void uploadFile_ChecksBucketOnlyOnce() throws Exception {
        when(amazonS3.doesBucketExistV2(BUCKET_NAME)).thenReturn(true);
        MultipartFile file = mockValidMultipartFile();

        s3Service.uploadFile(file);
        s3Service.uploadFile(file);

        verify(amazonS3, times(1)).doesBucketExistV2(BUCKET_NAME);
    }

    @Test
    void deleteFile_Success() {
        s3Service.deleteFile(KEY);

        verify(amazonS3).deleteObject(BUCKET_NAME, KEY);
    }

    @Test
    void deleteFile_ThrowsRuntimeExceptionWhenOnError() {
        doThrow(RuntimeException.class).when(amazonS3).deleteObject(BUCKET_NAME, KEY);

        assertThrows(RuntimeException.class, () -> s3Service.deleteFile(KEY));
    }

    @Test
    void generateKey_IncludesExtension() {
        String originalName = "photo.png";
        String key = s3Service.generateKey(originalName);

        assertThat(key).endsWith(".png");
        assertThat(key).hasSize(36 + 4);
    }

    @Test
    void generateKey_NoExtension() {
        String key = s3Service.generateKey("document");

        assertThat(key).hasSize(36);
        assertThat(key).doesNotContain(".");
    }

    @Test
    void generateKey_NullFileName() {
        String key = s3Service.generateKey(null);

        assertThat(key).hasSize(36);
        assertThat(key).doesNotContain(".");
    }
}
