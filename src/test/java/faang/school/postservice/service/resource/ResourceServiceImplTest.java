package faang.school.postservice.service.resource;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ResourceServiceImplTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private Resource resource;
    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);

        resource = new Resource();
        resource.setId(1L);
        resource.setName("test");
        resource.setKey("key");
        resource.setPost(post);

        List<Resource> resources = new ArrayList<>();
        resources.add(resource);

        post.setResources(resources);
    }

    @Test
    void testAddResource() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(s3Service.uploadFile(any(), anyString())).thenReturn(resource);

        MultipartFile multipartFile = prepareMultipartFileImage();

        resourceService.addResource(post.getId(), multipartFile);

        verify(postRepository, times(1)).findById(post.getId());
        verify(resourceRepository, times(1)).save(resource);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testAddResources_WhenFull() {
        List<Resource> resources = new ArrayList<>();
        fillResources(resource, resources);
        post.setResources(resources);

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(s3Service.uploadFile(any(), anyString())).thenReturn(resource);

        MultipartFile multipartFile = prepareMultipartFileImage();

        assertThrows(FileException.class, () -> resourceService.addResource(post.getId(), multipartFile));
    }

    @Test
    void testDeleteResource() {
        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));

        resourceService.deleteResource(resource.getId());

        verify(s3Service, times(1)).deleteFile(any());
        verify(postRepository, times(1)).save(post);
        verify(resourceRepository, times(1)).delete(resource);
    }

    @Test
    void testDownloadResource() {
        byte[] bytes = "test".getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));
        when(s3Service.downloadFile(anyString())).thenReturn(byteArrayInputStream);

        resourceService.downloadResource(resource.getId());

        verify(s3Service, times(1)).downloadFile(resource.getKey());
    }

    private MultipartFile prepareMultipartFileImage() {
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(Color.CYAN);
        graphics2D.fillRect(0, 0, 100, 100);
        graphics2D.dispose();

        byte[] bytes;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
        }
         catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new MockMultipartFile("file", "image.jpg", "image/jpeg", bytes);
    }

    private void fillResources(Resource resource, List<Resource> listToFill) {
        for (int i = 0; i < 10; i++) {
            listToFill.add(resource);
        }
    }
}