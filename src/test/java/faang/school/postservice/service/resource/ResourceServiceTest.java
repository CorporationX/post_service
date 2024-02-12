package faang.school.postservice.service.resource;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @InjectMocks
    private ResourceService resourceService;
    @Mock
    private PostService postService;
    @Mock
    private S3Service s3Service;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private PostRepository postRepository;
    @Captor
    private ArgumentCaptor<Resource> captor;
    @Mock
    private MultipartFile file;
    @Mock
    private Resource resource;
    private Post post;

    @BeforeEach
    void setUp() {
        List<Resource> resources = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
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
    }

    @Test
    void testAddResourceSuccessful() {
        Mockito.when(s3Service.uploadFile(file, "1/null")).thenReturn(resource);
        Mockito.when(postService.searchPostById(1L)).thenReturn(post);
        Mockito.when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        resourceService.addResource(1, file);
        captor = ArgumentCaptor.forClass(Resource.class);
        Mockito.verify(resourceRepository).save(captor.capture());
        assertEquals("name", captor.getValue().getName());
    }

    @Test
    void testAddResourceListResourceFilled() {
        Mockito.when(postService.searchPostById(1L)).thenReturn(post);
        post.getResources().add(resource);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> resourceService.addResource(1, file));
        assertEquals("A post can only have 10 images", exception.getMessage());
    }

    @Test
    void testDeleteResourceSuccessful() {
        Mockito.when(postService.searchPostById(1L)).thenReturn(post);
        Mockito.when(resourceRepository.getReferenceById(5L)).thenReturn(resource);
        resourceService.deleteResource(1, resource.getId());
        Mockito.verify(resourceRepository).delete(resource);
        Mockito.verify(s3Service).deleteFile("key");
    }

    @Test
    void testDeleteResourceResourceNotFound(){
        Mockito.when(postService.searchPostById(1L)).thenReturn(post);
        Mockito.when(resourceRepository.getReferenceById(2L)).thenReturn(resource);
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
            resourceService.deleteResource(1,2));
        assertEquals("Resource with id 2 does not belong to post with id 1", exception.getMessage());

    }
}