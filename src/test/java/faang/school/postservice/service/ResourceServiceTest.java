package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.ResourceService;
import faang.school.postservice.service.AmazonS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @InjectMocks
    private ResourceService resourceService;
    @Mock
    private PostService postService;
    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private ResourceRepository resourceRepository;
    @Spy
    private ResourceMapper resourceMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private Resource resource;
    private Post post;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        List<Resource> resources = new ArrayList<>(List.of());
        for (int i = 0; i < 8; i++) {
            resources.add(new Resource());
        }
        post = Post.builder()
                .id(1L)
                .resources(resources)
                .build();
        resource = Resource.builder()
                .id(5L)
                .name("name")
                .key("key")
                .build();
        mockFile = new MockMultipartFile(
                "file",
                "filename.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );
        assertNotNull(mockFile);
    }

    @Test
    public void testAddResourceMaxFiles() {
        ReflectionTestUtils.setField(resourceService, "maxFilesAmount", 10);
        post.getResources().add(resource);
        post.getResources().add(resource);
        Mockito.when(postService.searchPostById(anyLong())).thenReturn(post);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> resourceService.addResource(1L, List.of(mockFile)));
        assertEquals("The maximum number of images for the post has been exceeded", exception.getMessage());
    }

    @Test
    public void testAddResourceSuccessful() {
        Mockito.when(postService.searchPostById(1L)).thenReturn(post);
        Mockito.when(amazonS3Service.uploadFile(Mockito.any(), Mockito.anyString())).thenReturn(resource);
        resourceService.addResource(1, List.of(mockFile));
        assertEquals(10, post.getResources().size() + 1);
    }

    @Test
    void testDeleteResourceSuccessful() {
        Mockito.when(postService.searchPostById(1L)).thenReturn(post);
        Mockito.when(resourceRepository.getReferenceById(5L)).thenReturn(resource);
        resourceService.deleteResource(1, resource.getId());
        Mockito.verify(resourceRepository).delete(resource);
        Mockito.verify(amazonS3Service).deleteFile("key");
    }

    @Test
    void testDeleteResourceResourceNotFound() {
        Mockito.when(postService.searchPostById(1L)).thenReturn(post);
        Mockito.when(resourceRepository.getReferenceById(2L)).thenReturn(resource);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                resourceService.deleteResource(1, 2));
        assertEquals("Resource with id 2 does not belong to post with id 1", exception.getMessage());

    }
}