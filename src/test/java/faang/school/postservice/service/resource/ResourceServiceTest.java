package faang.school.postservice.service.resource;

import faang.school.postservice.model.dto.resource.ResourceDto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.mapper.resource.ResourceMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.resource.Resource;
import faang.school.postservice.model.resource.ResourceStatus;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.util.resource.ImageResizer;
import faang.school.postservice.validator.resource.ResourceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    private Post post;
    private Resource resource;
    private ResourceDto resourceDto;
    private MultipartFile imageFile;

    @Mock
    private S3Service s3Service;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ImageResizer imageResizer;

    @Mock
    private ResourceValidator resourceValidator;

    @Spy
    private ResourceMapper resourceMapper = new ResourceMapperImpl();

    @InjectMocks
    private ResourceService resourceService;

    @BeforeEach
    public void setUp() {
        post = new Post();
        post.setId(1L);
        post.setResources(new ArrayList<>());

        resource = new Resource();
        resource.setId(1L);
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setSize(3000L);

        resourceDto = new ResourceDto();
        resourceDto.setId(1L);
        resourceDto.setStatus(ResourceStatus.ACTIVE);
        resourceDto.setSize("2,93 kilobytes");

        imageFile = Mockito.mock(MultipartFile.class);

        ReflectionTestUtils.setField(resourceService, "retentionPeriod", "1M");
    }

    @Test
    @DisplayName("Should attach images successfully and update post resources")
    public void testAttachImages_Success() throws Exception {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        doNothing().when(resourceValidator).validateImages(anyList(), anyList());
        when(imageResizer.resizeImage(any())).thenReturn(imageFile);
        when(s3Service.uploadFiles(anyList(), anyString())).thenReturn(Collections.singletonList(resource));

        List<ResourceDto> result = resourceService.attachImages(1L, Collections.singletonList(imageFile));

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertTrue(post.getResources().contains(resource)),
                () -> assertEquals(resourceDto, result.get(0))
        );

        verify(postRepository).save(post);
        verify(resourceRepository).save(resource);
        verify(imageResizer).resizeImage(any());
        verify(s3Service).uploadFiles(anyList(), anyString());
    }

    @Test
    @DisplayName("Should mark resource as deleted and update timestamp")
    public void testDeleteResource_Success() {
        LocalDateTime now = LocalDateTime.now();
        resource.setUpdatedAt(now);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        ResourceDto deletedResource = resourceService.deleteResource(1L);

        assertAll(
                () -> assertNotNull(deletedResource),
                () -> assertEquals(ResourceStatus.DELETED, deletedResource.getStatus()),
                () -> assertTrue(deletedResource.getUpdatedAt().isAfter(now))
        );

        verify(resourceRepository).save(resource);
    }

    @Test
    @DisplayName("Should restore deleted resource and update timestamp")
    public void testRestoreResource() {
        LocalDateTime now = LocalDateTime.now();
        resource.setUpdatedAt(now);
        resource.setStatus(ResourceStatus.DELETED);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        ResourceDto restoredResource = resourceService.restoreResource(1L);

        assertAll(
                () -> assertNotNull(restoredResource),
                () -> assertEquals(ResourceStatus.ACTIVE, restoredResource.getStatus()),
                () -> assertTrue(restoredResource.getUpdatedAt().isAfter(now))
        );

        verify(resourceRepository).save(resource);
    }

    @Test
    @DisplayName("Should delete old resources marked as deleted and remove from S3")
    public void testDeleteOldDeletedResources() {
        resource.setUpdatedAt(LocalDateTime.now().minusMonths(2));
        resource.setStatus(ResourceStatus.DELETED);

        when(resourceRepository.findAllByStatusAndUpdatedAtBefore(eq(ResourceStatus.DELETED), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(resource));

        resourceService.deleteOldDeletedResources();

        verify(s3Service).deleteFile(resource.getKey());
        verify(resourceRepository).delete(resource);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when post is not found")
    public void testGetPostById_NotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                resourceService.attachImages(1L, Collections.singletonList(imageFile)));

        assertEquals("Post not found with id: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when resource is not found")
    public void testGetResourceById_NotFound() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                resourceService.deleteResource(1L));

        assertEquals("Resource not found with id: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when resource is already deleted")
    public void testDeleteResource_ThrowsIllegalStateException() {
        resource.setStatus(ResourceStatus.DELETED);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            resourceService.deleteResource(1L);
        });

        assertEquals("Resource is already in 'DELETED' status!", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when resource is already active")
    public void testRestoreResource_ThrowsIllegalStateException() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            resourceService.restoreResource(1L);
        });

        assertEquals("Resource is already in 'ACTIVE' status!", exception.getMessage());
    }
}
