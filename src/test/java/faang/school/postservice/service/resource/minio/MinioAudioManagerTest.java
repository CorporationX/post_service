package faang.school.postservice.service.resource.minio;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ResourceEntity;
import faang.school.postservice.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

public class MinioAudioManagerTest {
    AmazonS3 s3client = mock(AmazonS3.class);
    MinioAudioManager minioAudioManager = new MinioAudioManager(s3client);

    MultipartFile file = mock(MultipartFile.class);
    Post post;
    String fileName;
    String contentType;
    InputStream inputStream;
    String key;
    String bucketName;

    @BeforeEach
    public void setUp() {
        bucketName = "test_bucket";
        ReflectionTestUtils.setField(minioAudioManager, "bucketName", bucketName);

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

        ResourceEntity resourceEntity = minioAudioManager.addFileToStorage(file, post);

        assertNotNull(resourceEntity);
        assertEquals(fileName, resourceEntity.getName());
        assertEquals(ResourceType.AUDIO, resourceEntity.getType());
        assertEquals(post, resourceEntity.getPost());

        inputStream.close();
    }

    @Test
    @DisplayName("testUpdateFileToStorage")
    public void testUpdateFileToStorage() throws IOException {
        when(file.getContentType()).thenReturn(contentType);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getSize()).thenReturn((long) inputStream.available());

        ResourceEntity resourceEntity = minioAudioManager.updateFileInStorage(key, file, post);

        assertNotNull(resourceEntity);
        assertEquals(fileName, resourceEntity.getName());
        assertEquals(ResourceType.AUDIO, resourceEntity.getType());
        assertEquals(post, resourceEntity.getPost());

        inputStream.close();
    }

    @Test
    @DisplayName("testRemoveFileInStorage")
    public void testRemoveFileInStorage() throws IOException {
        minioAudioManager.removeFileInStorage(key);
        verify(s3client, times(1)).deleteObject(bucketName, key);

        inputStream.close();
    }
}
