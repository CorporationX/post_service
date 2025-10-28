package faang.school.postservice.service.minio;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import faang.school.postservice.config.s3.MinioConfig;
import faang.school.postservice.exceptions.FileException;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinioServiceImplTest {

    @Mock
    private MinioConfig minioConfig;

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private MinioServiceImpl minioService;

    @BeforeEach
    void setUp() throws Exception {
        when(minioConfig.s3Client()).thenReturn(s3Client);

        setField(minioService, "bucketName", "test-bucket");
        setField(minioService, "MAX_LIVE_URL_MINUTES", 60);
    }

    @Test
    void uploadImage_success_shouldReturnResource() {
        byte[] imageData = "test image data".getBytes();
        String folder = "posts/1";
        String originalFileName = "test.jpg";
        String contentType = "image/jpeg";

        Resource result = minioService.uploadImage(imageData, folder, originalFileName, contentType);

        assertNotNull(result);
        assertEquals(originalFileName, result.getName());
        assertEquals(contentType, result.getType());
        assertEquals(imageData.length, result.getSize());
        assertTrue(result.getKey().contains(folder));
        assertTrue(result.getKey().contains(originalFileName));

        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void uploadImage_exception_shouldThrowFileException() {
        byte[] imageData = "test image data".getBytes();
        String folder = "posts/1";
        String originalFileName = "test.jpg";
        String contentType = "image/jpeg";

        doThrow(new RuntimeException("S3 error")).when(s3Client).putObject(any(PutObjectRequest.class));

        assertThrows(FileException.class,
                () -> minioService.uploadImage(imageData, folder, originalFileName, contentType));
    }

    @Test
    void downloadImage_success_shouldReturnUrls() {
        String key = "posts/1/test.jpg";
        String folder = "posts/1";

        S3ObjectSummary objectSummary = new S3ObjectSummary();
        objectSummary.setKey(key);

        ListObjectsV2Result listResult = new ListObjectsV2Result();
        listResult.getObjectSummaries().add(objectSummary);

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResult);

        URL mockUrl;
        try {
            mockUrl = new URL("https://test.com/file.jpg");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        when(s3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(mockUrl);

        List<String> result = minioService.downloadImage(key, folder);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://test.com/file.jpg", result.get(0));

        verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
        verify(s3Client).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
    }

    @Test
    void downloadImage_emptyFolder_shouldReturnEmptyList() {
        String key = "posts/1/test.jpg";
        String folder = "posts/1";

        ListObjectsV2Result listResult = new ListObjectsV2Result();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResult);

        List<String> result = minioService.downloadImage(key, folder);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void downloadImage_onlyFolderObject_shouldReturnEmptyList() {
        String key = "posts/1/test.jpg";
        String folder = "posts/1";

        S3ObjectSummary folderObject = new S3ObjectSummary();
        folderObject.setKey("posts/1/");

        ListObjectsV2Result listResult = new ListObjectsV2Result();
        listResult.getObjectSummaries().add(folderObject);

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResult);

        List<String> result = minioService.downloadImage(key, folder);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void downloadImage_amazonS3Exception_shouldThrowFileException() {
        String key = "posts/1/test.jpg";
        String folder = "posts/1";

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenThrow(new AmazonS3Exception("S3 error"));

        assertThrows(FileException.class,
                () -> minioService.downloadImage(key, folder));
    }

    @Test
    void deleteImage_success_shouldDeleteObject() {
        String key = "posts/1/test.jpg";

        minioService.deleteImage(key);

        verify(s3Client).deleteObject("test-bucket", key);
    }

    @Test
    void deleteImage_amazonS3Exception_shouldThrowFileException() {
        String key = "posts/1/test.jpg";

        doThrow(new AmazonS3Exception("Delete error"))
                .when(s3Client).deleteObject("test-bucket", key);

        assertThrows(FileException.class,
                () -> minioService.deleteImage(key));
    }

    @Test
    void generateTemporaryUrl_success_shouldReturnUrl() {
        String key = "posts/1/test.jpg";
        String folder = "posts/1";

        S3ObjectSummary objectSummary = new S3ObjectSummary();
        objectSummary.setKey(key);

        ListObjectsV2Result listResult = new ListObjectsV2Result();
        listResult.getObjectSummaries().add(objectSummary);

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResult);

        URL mockUrl;
        try {
            mockUrl = new URL("https://test.com/file.jpg");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        when(s3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(mockUrl);

        List<String> result = minioService.downloadImage(key, folder);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://test.com/file.jpg", result.get(0));
    }

    @Test
    void generateTemporaryUrl_amazonS3Exception_shouldThrowFileException() {
        String key = "posts/1/test.jpg";

        S3ObjectSummary objectSummary = new S3ObjectSummary();
        objectSummary.setKey(key);

        ListObjectsV2Result listResult = new ListObjectsV2Result();
        listResult.getObjectSummaries().add(objectSummary);

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResult);
        when(s3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenThrow(new AmazonS3Exception("URL generation error"));

        assertThrows(FileException.class,
                () -> minioService.downloadImage(key, "posts/1"));
    }

    private void setField(Object target, String fieldName, Object value) {
        Field field;
        try {
            field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}