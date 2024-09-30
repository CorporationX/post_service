package faang.school.postservice.service.resource.s3;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

public class S3AudioServiceTest {
    AmazonS3 s3client = mock(AmazonS3.class);
    S3AudioService s3AudioService = new S3AudioService(s3client);

    MultipartFile file = mock(MockMultipartFile.class);
    Post post;
    String fileName;
    String contentType;
    InputStream inputStream;
    String key;
    String bucketName;

    @BeforeEach
    public void setUp() {
        bucketName = "test_bucket";
        ReflectionTestUtils.setField(s3AudioService, "bucketName", bucketName);

        post = new Post();
        post.setId(1L);
        fileName = "audio.jpg";
        contentType = "audio/jpeg";
        inputStream = new ByteArrayInputStream("test_audio_content".getBytes());
        key = "key";
    }

    @Test
    @DisplayName("testAddFileToStorage")
    public void testAddFileToStorage() throws IOException {
        when(file.getContentType()).thenReturn(contentType);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getSize()).thenReturn((long) inputStream.available());

        Resource resource = s3AudioService.addFileToStorage(file, post);

        assertNotNull(resource);
        assertEquals(fileName, resource.getName());
        assertEquals(ResourceType.AUDIO, resource.getType());
        assertEquals(post, resource.getPost());

        inputStream.close();
    }

    @Test
    @DisplayName("testUpdateFileToStorage")
    public void testUpdateFileToStorage() throws IOException {
        when(file.getContentType()).thenReturn(contentType);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getSize()).thenReturn((long) inputStream.available());

        Resource resource = s3AudioService.updateFileInStorage(key, file, post);

        assertNotNull(resource);
        assertEquals(fileName, resource.getName());
        assertEquals(ResourceType.AUDIO, resource.getType());
        assertEquals(post, resource.getPost());

        inputStream.close();
    }

    @Test
    @DisplayName("testRemoveFileInStorage")
    public void testRemoveFileInStorage() throws IOException {
        s3AudioService.removeFileInStorage(key);
        verify(s3client, times(1)).deleteObject(bucketName, key);

        inputStream.close();
    }
}
