package faang.school.postservice.service.resource.s3;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
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
public class S3ImageManagerTest {
    AmazonS3 s3client = mock(AmazonS3.class);
    S3ImageManager s3ImageManager = new S3ImageManager(s3client);

    MultipartFile file = mock(MockMultipartFile.class);
    Post post;
    String fileName;
    String contentType;
    InputStream inputStream;
    String key;
    String bucketName;

    @BeforeEach
    public void setUp() throws FileNotFoundException {
        bucketName = "test_bucket";
        ReflectionTestUtils.setField(s3ImageManager, "bucketName", bucketName);

        post = new Post();
        post.setId(1L);
        fileName = "image.jpg";
        contentType = "image/jpeg";
        File imageFile = new File("src/test/java/faang/school/postservice/service/resource/s3/test_image.jpg");
        inputStream = new FileInputStream(imageFile);
        key = "key";
    }

    @Test
    @DisplayName("testAddFileToStorage")
    public void testAddFileToStorage() throws IOException {
        when(file.getContentType()).thenReturn(contentType);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(inputStream);

        Resource resource = s3ImageManager.addFileToStorage(file, post);

        assertNotNull(resource);
        assertEquals(fileName, resource.getName());
        assertEquals(ResourceType.IMAGE, resource.getType());
        assertEquals(post, resource.getPost());

        inputStream.close();
    }

    @Test
    @DisplayName("testUpdateFileInStorage")
    public void testUpdateFileInStorage() throws IOException {
        when(file.getContentType()).thenReturn(contentType);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(inputStream);

        Resource resource = s3ImageManager.updateFileInStorage(key, file, post);

        assertNotNull(resource);
        assertEquals(fileName, resource.getName());
        assertEquals(ResourceType.IMAGE, resource.getType());
        assertEquals(post, resource.getPost());

        inputStream.close();
    }

    @Test
    @DisplayName("testRemoveFileInStorage")
    public void testRemoveFileInStorage() throws IOException {
        s3ImageManager.removeFileInStorage(key);
        verify(s3client, times(1)).deleteObject(bucketName, key);

        inputStream.close();
    }
}
