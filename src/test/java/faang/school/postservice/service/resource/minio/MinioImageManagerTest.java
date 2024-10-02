package faang.school.postservice.service.resource.minio;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ResourceEntity;
import faang.school.postservice.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MinioImageManagerTest {
    AmazonS3 s3client = mock(AmazonS3.class);
    MinioImageManager minioImageManager = new MinioImageManager(s3client);

    MultipartFile file = mock(MultipartFile.class);
    Post post;
    String fileName;
    String contentType;
    InputStream inputStream;
    String key;
    String bucketName;

    @BeforeEach
    public void setUp() throws FileNotFoundException {
        bucketName = "test_bucket";
        ReflectionTestUtils.setField(minioImageManager, "bucketName", bucketName);

        post = new Post();
        post.setId(1L);
        fileName = "image.jpg";
        contentType = "image/jpeg";
        File imageFile = new File("src/main/resources/test/test_image.jpg");
        inputStream = new FileInputStream(imageFile);
        key = "key";
    }

    @Test
    @DisplayName("testAddFileToStorage")
    public void testAddFileToStorage() throws IOException {
        when(file.getContentType()).thenReturn(contentType);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(inputStream);

        ResourceEntity resourceEntity = minioImageManager.addFileToStorage(file, post);

        assertNotNull(resourceEntity);
        assertEquals(fileName, resourceEntity.getName());
        assertEquals(ResourceType.IMAGE, resourceEntity.getType());
        assertEquals(post, resourceEntity.getPost());

        inputStream.close();
    }

    @Test
    @DisplayName("testUpdateFileInStorage")
    public void testUpdateFileInStorage() throws IOException {
        when(file.getContentType()).thenReturn(contentType);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(inputStream);

        ResourceEntity resourceEntity = minioImageManager.updateFileInStorage(key, file, post);

        assertNotNull(resourceEntity);
        assertEquals(fileName, resourceEntity.getName());
        assertEquals(ResourceType.IMAGE, resourceEntity.getType());
        assertEquals(post, resourceEntity.getPost());

        inputStream.close();
    }

    @Test
    @DisplayName("testRemoveFileInStorage")
    public void testRemoveFileInStorage() throws IOException {
        minioImageManager.removeFileInStorage(key);
        verify(s3client, times(1)).deleteObject(bucketName, key);

        inputStream.close();
    }
}
