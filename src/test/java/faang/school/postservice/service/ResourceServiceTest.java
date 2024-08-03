package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.resource.ResizeService;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.validator.ResourceServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ResizeService resizeService;

    @InjectMocks
    private ResourceService resourceService;

    @Mock
    private ResourceServiceValidator resourceServiceValidator;


    @Test
    @DisplayName("testAddImage - success")
    void testAddImageSuccess() throws IOException {
        Long postId = 1L;
        MultipartFile imageFile = mock(MultipartFile.class);
        MultipartFile resizedImageFile = mock(MultipartFile.class);

        Post post = Post.builder()
                .resources(new ArrayList<>())
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(resizeService.resizeImage(imageFile)).thenReturn(resizedImageFile);
        when(s3Service.uploadFile(resizedImageFile, "post" + postId + "image")).thenReturn(new Resource());
        doNothing().when(resourceServiceValidator).validateResourceSize(anyLong());
        doNothing().when(resourceServiceValidator).checkIfFileAreImages(any(MultipartFile.class));

        ResponseEntity<String> response = resourceService.addImage(postId, imageFile);

        assertEquals("Resource added successfully", response.getBody());
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("testAddImages - success")
    void testAddImagesSuccess() {
        Long postId = 1L;
        List<MultipartFile> imageFiles = Collections.singletonList(mock(MultipartFile.class));

        Post post = Post.builder()
                .resources(new ArrayList<>())
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(s3Service.uploadFiles(any(), anyString())).thenReturn(Collections.singletonList(new Resource()));
        doNothing().when(resourceServiceValidator).validateResourceSize(anyLong());
        doNothing().when(resourceServiceValidator).checkIfFileAreImages(any(MultipartFile.class));

        ResponseEntity<String> response = resourceService.addImages(postId, imageFiles);

        assertEquals("Resources added successfully", response.getBody());
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("testDeleteResource - success")
    void testDeleteResourceSuccess() {
        List<Resource> resources = new ArrayList<>();
        resources.add(new Resource());
        Long resourceId = 1L;
        Resource resource = Resource.builder()
                .id(resourceId)
                .key("resourceKey")
                .post(Post.builder().resources(resources).build())
                .build();


        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        doNothing().when(s3Service).deleteFile(resource.getKey());

        ResponseEntity<String> response = resourceService.deleteResource(resourceId);

        assertEquals("Resource deleted successfully", response.getBody());
        verify(s3Service).deleteFile(resource.getKey());
        verify(resourceRepository).deleteById(resourceId);
    }

    @Test
    @DisplayName("testDownloadResource - success")
    void testDownloadResourceSuccess() {
        Long resourceId = 1L;
        Resource resource = new Resource();
        resource.setKey("resourceKey");

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(s3Service.downloadFile(resource.getKey())).thenReturn(mock(InputStream.class));

        InputStream inputStream = resourceService.downloadResource(resourceId);

        assertNotNull(inputStream);
    }

    @Test
    @DisplayName("testAddImages - post not found")
    void testAddImagesPostNotFound() {
        Long postId = 1L;
        List<MultipartFile> imageFiles = Collections.singletonList(mock(MultipartFile.class));

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                resourceService.addImages(postId, imageFiles)
        );

        assertEquals("Post " + postId + " not found", exception.getMessage());
    }

    @Test
    @DisplayName("testAddImage - post not found")
    void testAddImagePostNotFound() {
        Long postId = 1L;
        MultipartFile imageFile = mock(MultipartFile.class);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                resourceService.addImage(postId, imageFile)
        );

        assertEquals("Post " + postId + " not found", exception.getMessage());
    }

    @Test
    @DisplayName("testDeleteResource - resource not found")
    void testDeleteResourceResourceNotFound() {
        Long resourceId = 1L;

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                resourceService.deleteResource(resourceId)
        );

        assertEquals("Resource id: " + resourceId + " not found", exception.getMessage());
    }

    @Test
    @DisplayName("testDownloadResource - resource not found")
    void testDownloadResourceResourceNotFound() {
        Long resourceId = 1L;

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                resourceService.downloadResource(resourceId)
        );

        assertEquals("Resource id: " + resourceId + " not found", exception.getMessage());
    }
}