package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceRequest;
import faang.school.postservice.dto.resource.ResourceResponse;
import faang.school.postservice.exceptions.FileException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.utils.ImageService;
import faang.school.postservice.utils.MinioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Spy
    private ResourceMapper resourceMapper = Mappers.getMapper(ResourceMapper.class);

    @Mock
    private ImageService imageService;

    @Mock
    private MinioService minioService;

    private ResourceService resourceService;

    private Post post;
    private MockMultipartFile file;
    private Resource resource;

    @BeforeEach
    public void setUp() throws Exception {
        resourceService = new ResourceService(postRepository, resourceRepository, resourceMapper, imageService, minioService);

        post = new Post();
        post.setId(1L);

        file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});

        resource = new Resource();
        resource.setId(1L);
        resource.setKey("test-key");
        resource.setName("test.jpg");
        resource.setSize(file.getSize());
        resource.setCreatedAt(LocalDateTime.now());
        resource.setType(file.getContentType());
        resource.setPost(post);
    }

    @Test
    void addFileToPost_Success() throws Exception {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(resourceRepository.findByPostId(1L)).thenReturn(Collections.emptyList());

        when(minioService.uploadFile(any(byte[].class), anyString(), anyString())).thenReturn("test-key");
        when(resourceRepository.save(any(Resource.class))).thenAnswer(invocation -> {
            Resource r = invocation.getArgument(0, Resource.class);
            r.setId(1L);
            return r;
        });

        BufferedImage image = null;

        ResourceResponse response = resourceService.addFileToPost(1L, file);

        assertNotNull(response);
        assertEquals("test-key", response.key());
        assertEquals("test.jpg", response.name());
        assertEquals(file.getSize(), response.size());
        assertEquals(file.getContentType(), response.type());

        verify(postRepository).findById(1L);
        verify(resourceRepository).findByPostId(1L);
        verify(minioService).uploadFile(any(byte[].class), anyString(), anyString());
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    void addFileToPost_Fail_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                resourceService.addFileToPost(1L, file)
        );

        assertEquals("Post не найден", exception.getMessage());
        verify(postRepository).findById(1L);
        verifyNoInteractions(resourceRepository, minioService);
    }

    @Test
    void addFileToPost_Fail_FileLimitExceeded() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(resourceRepository.findByPostId(1L)).thenReturn(Collections.nCopies(10, resource));

        Exception exception = assertThrows(IllegalStateException.class, () ->
                resourceService.addFileToPost(1L, file)
        );

        assertEquals("Можно загрузить не более 10 изображений.", exception.getMessage());
    }

    @Test
    void addFileToPost_Fail_FileTooLarge() {
        byte[] largeContent = new byte[6 * 1024 * 1024];
        MockMultipartFile largeFile = new MockMultipartFile("file", "big.jpg", "image/jpeg", largeContent);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Exception exception = assertThrows(FileException.class, () ->
                resourceService.addFileToPost(1L, largeFile)
        );

        assertEquals("Размер файла не должен превышать 5 МБ", exception.getMessage());
    }

    @Test
    void addFilesToPost_Success() throws Exception {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        when(minioService.uploadFile(any(byte[].class), anyString(), anyString())).thenReturn("test-key");
        when(resourceRepository.save(any(Resource.class))).thenAnswer(invocation -> {
            Resource r = invocation.getArgument(0, Resource.class);
            r.setId(1L);
            return r;
        });
        List<MultipartFile> files = List.of(file, file);
        List<ResourceResponse> responses = resourceService.addFilesToPost(1L, files);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        responses.forEach(resp -> {
            assertEquals("test-key", resp.key());
            assertEquals("test.jpg", resp.name());
        });
    }

    @Test
    void removeFileFromPost_Success() {
        ResourceRequest request = new ResourceRequest(
                1L,
                "test-key",
                123L,
                "test.jpg",
                "image/jpeg",
                1L
        );

        doNothing().when(resourceRepository).deleteById(1L);
        doNothing().when(minioService).completeRemoval("test-key");

        assertDoesNotThrow(() -> resourceService.removeFileFromPost(request));

        verify(resourceRepository).deleteById(1L);
        verify(minioService).completeRemoval("test-key");
    }

    @Test
    void removeFileFromPost_Fail_ExceptionThrown() {
        ResourceRequest request = new ResourceRequest(
                1L,
                "test-key",
                123L,
                "test.jpg",
                "image/jpeg",
                1L
        );

        doThrow(new RuntimeException("Ошибка")).when(resourceRepository).deleteById(1L);

        Exception exception = assertThrows(RuntimeException.class, () ->
                resourceService.removeFileFromPost(request)
        );

        assertEquals("Ошибка", exception.getMessage());
        verify(resourceRepository).deleteById(1L);
        verify(minioService, never()).completeRemoval("test-key");
    }

    @Test
    void getFilesForPost_Success() {
        when(resourceRepository.findByPostId(1L)).thenReturn(List.of(resource));
        when(minioService.getFile("post-images", "test-key")).thenReturn(new byte[]{1, 2, 3});

        List<byte[]> files = resourceService.getFilesForPost(1L);

        assertNotNull(files);
        assertEquals(1, files.size());
        assertArrayEquals(new byte[]{1, 2, 3}, files.get(0));

        verify(resourceRepository).findByPostId(1L);
        verify(minioService).getFile("post-images", "test-key");
    }

    @Test
    void getFilesForPost_EmptyList() {
        when(resourceRepository.findByPostId(1L)).thenReturn(Collections.emptyList());

        List<byte[]> files = resourceService.getFilesForPost(1L);

        assertNotNull(files);
        assertTrue(files.isEmpty());
        verify(resourceRepository).findByPostId(1L);
        verify(minioService, never()).getFile(anyString(), anyString());
    }
}
