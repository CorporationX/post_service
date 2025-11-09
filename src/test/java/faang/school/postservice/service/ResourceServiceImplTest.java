package faang.school.postservice.service;

import faang.school.postservice.model.resource.Resource;
import faang.school.postservice.model.resource.ResourceType;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.image.ImageProcessor;
import faang.school.postservice.service.resource.ResourceServiceImpl;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.validator.ResourceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageProcessor imageProcessor;

    @Mock
    private ResourceValidator resourceValidator;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private final static Long POST_ID = 1L;
    private final static String FILE_KEY = "file-key";
    private final static Long FILE_SIZE = 1024L;

    private MultipartFile createMockFile(String name, String contentType) {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(contentType);
        when(file.getOriginalFilename()).thenReturn(name);
        when(file.getSize()).thenReturn(FILE_SIZE);
        return file;
    }

    private Resource createResource(Long id, String key, String name) {
        return Resource.builder()
                .id(id)
                .key(key)
                .name(name)
                .type(ResourceType.IMAGE)
                .postId(POST_ID)
                .size(FILE_SIZE)
                .build();
    }

    @Test
    void uploadResourcesForPost_Success() {
        MultipartFile file1 = createMockFile("image1.jpg", "image/jpeg");
        MultipartFile file2 = createMockFile("image2.png", "image/png");
        List<MultipartFile> files = Arrays.asList(file1, file2);

        Resource resource1 = createResource(1L, "key1", "image1.jpg");
        Resource resource2 = createResource(2L, "key2", "image2.png");

        setupCommonValidatorMocks(files);

        when(imageProcessor.needsCompression(any(MultipartFile.class))).thenReturn(false);
        when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn(FILE_KEY);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource1, resource2);

        List<Resource> result = resourceService.uploadResourcesForPost(files, POST_ID);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(resourceValidator, times(1)).validatePostExists(POST_ID);
        verify(resourceValidator, times(1)).validateFiles(files);
        verify(resourceValidator, times(1)).validateFileLimit(POST_ID, files.size());
        verify(s3Service, times(2)).uploadFile(any(MultipartFile.class));
        verify(resourceRepository, times(2)).save(any(Resource.class));
    }

    @Test
    void uploadResourcesForPost_WithImageCompression() {
        MultipartFile originalFile = mock(MultipartFile.class);
        MultipartFile compressedFile = mock(MultipartFile.class);

        when(originalFile.getContentType()).thenReturn("image/jpeg");

        when(compressedFile.getContentType()).thenReturn("image/jpeg");
        when(compressedFile.getOriginalFilename()).thenReturn("compressed.jpg");
        when(compressedFile.getSize()).thenReturn(FILE_SIZE);

        List<MultipartFile> files = List.of(originalFile);
        Resource resource = createResource(1L, FILE_KEY, "compressed-image.jpg");

        setupCommonValidatorMocks(files);

        when(imageProcessor.needsCompression(originalFile)).thenReturn(true);
        when(imageProcessor.compressImage(originalFile)).thenReturn(compressedFile);
        when(s3Service.uploadFile(compressedFile)).thenReturn(FILE_KEY);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        List<Resource> result = resourceService.uploadResourcesForPost(files, POST_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(imageProcessor, times(1)).compressImage(originalFile);
        verify(s3Service, times(1)).uploadFile(compressedFile);
        verify(s3Service, never()).uploadFile(originalFile);
    }

    @Test
    void uploadResourcesForPost_RollbackOnUploadFailure() {
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);

        when(file1.getContentType()).thenReturn("image/jpeg");
        when(file1.getOriginalFilename()).thenReturn("success.jpg");
        when(file1.getSize()).thenReturn(FILE_SIZE);

        when(file2.getContentType()).thenReturn("image/jpeg");
        when(file2.getOriginalFilename()).thenReturn("fail.jpg");

        List<MultipartFile> files = Arrays.asList(file1, file2);
        Resource successResource = createResource(1L, "success-key", "success.jpg");

        setupCommonValidatorMocks(files);

        when(imageProcessor.needsCompression(file1)).thenReturn(false);
        when(imageProcessor.needsCompression(file2)).thenReturn(false);

        when(s3Service.uploadFile(file1)).thenReturn("success-key");
        when(s3Service.uploadFile(file2)).thenThrow(new RuntimeException("S3 upload failed"));
        when(resourceRepository.save(any(Resource.class))).thenReturn(successResource);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                resourceService.uploadResourcesForPost(files, POST_ID));

        assertTrue(exception.getMessage().contains("Failed to upload file: fail.jpg"));
        verify(s3Service).deleteFile("success-key");
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    void uploadResourcesForPost_EmptyFilesList() {
        List<MultipartFile> emptyFiles = List.of();

        setupCommonValidatorMocks(emptyFiles);

        List<Resource> result = resourceService.uploadResourcesForPost(emptyFiles, POST_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(s3Service, never()).uploadFile(any(MultipartFile.class));
        verify(resourceRepository, never()).save(any(Resource.class));
    }

    @Test
    void updatePostResources_Success() {
        MultipartFile newFile = createMockFile("new-image.jpg", "image/jpeg");
        List<MultipartFile> newFiles = List.of(newFile);
        List<Long> filesToDelete = Arrays.asList(1L, 2L);

        Resource resource1 = createResource(1L, "key1", "file1.jpg");
        Resource resource2 = createResource(2L, "key2", "file2.jpg");
        List<Resource> resourcesToDelete = Arrays.asList(resource1, resource2);

        Resource newResource = createResource(3L, "new-key", "new-image.jpg");

        doNothing().when(resourceValidator).validatePostExists(POST_ID);
        doNothing().when(resourceValidator).validateFileLimitOnUpdate(POST_ID, newFiles.size(), filesToDelete.size());

        when(resourceRepository.findAllById(filesToDelete)).thenReturn(resourcesToDelete);

        when(resourceRepository.save(any(Resource.class))).thenReturn(newResource);
        when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn("new-key");

        resourceService.updatePostResources(POST_ID, newFiles, filesToDelete);

        verify(resourceValidator, times(2)).validatePostExists(POST_ID);
        verify(resourceValidator, times(1)).validateFileLimitOnUpdate(POST_ID, 1, 2);

        verify(resourceRepository).findAllById(filesToDelete);

        verify(s3Service, times(2)).deleteFile(anyString());
        verify(s3Service).deleteFile("key1");
        verify(s3Service).deleteFile("key2");

        verify(resourceRepository).deleteAllById(filesToDelete);
    }

    @Test
    void updatePostResources_RollbackWhenDeleteFails() {
        MultipartFile newFile = mock(MultipartFile.class);

        when(newFile.getContentType()).thenReturn("image/jpeg");
        when(newFile.getOriginalFilename()).thenReturn("new-image.jpg");
        when(newFile.getSize()).thenReturn(FILE_SIZE);

        List<MultipartFile> newFiles = List.of(newFile);
        List<Long> filesToDelete = List.of(1L);

        Resource resourceToDelete = createResource(1L, "old-key", "old.jpg");
        Resource newResource = createResource(2L, "new-key", "new-image.jpg");

        doNothing().when(resourceValidator).validatePostExists(POST_ID);
        doNothing().when(resourceValidator).validateFileLimitOnUpdate(POST_ID, 1, 1);

        when(resourceRepository.findAllById(filesToDelete)).thenReturn(List.of(resourceToDelete));
        when(imageProcessor.needsCompression(newFile)).thenReturn(false);
        when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn("new-key");
        when(resourceRepository.save(any(Resource.class))).thenReturn(newResource);

        doThrow(new RuntimeException("Delete failed")).when(resourceRepository).deleteAllById(filesToDelete);

        assertThrows(RuntimeException.class, () ->
                resourceService.updatePostResources(POST_ID, newFiles, filesToDelete));

        verify(s3Service, times(1)).deleteFile("new-key");
        verify(s3Service, times(1)).deleteFile("old-key");
    }

    @Test
    void getResourcesByPostId_Success() {
        List<Resource> expectedResources = Arrays.asList(
                createResource(1L, "key1", "img1.jpg"),
                createResource(2L, "key2", "img2.jpg")
        );

        when(resourceRepository.findByPostId(POST_ID)).thenReturn(expectedResources);

        List<Resource> result = resourceService.getResourcesByPostId(POST_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedResources, result);
        verify(resourceRepository, times(1)).findByPostId(POST_ID);
    }

    @Test
    void deletePostResources_Success() {
        List<Resource> resources = Arrays.asList(
                createResource(1L, "key1", "img1.jpg"),
                createResource(2L, "key2", "img2.jpg")
        );

        when(resourceRepository.findByPostId(POST_ID)).thenReturn(resources);

        resourceService.deletePostResources(POST_ID);

        verify(resourceValidator, times(1)).validatePostExists(POST_ID);
        verify(s3Service, times(1)).deleteFile("key1");
        verify(s3Service, times(1)).deleteFile("key2");
        verify(resourceRepository, times(1)).deleteByPostId(POST_ID);
    }

    @Test
    void deletePostResources_NoResourcesFound() {
        when(resourceRepository.findByPostId(POST_ID)).thenReturn(List.of());

        resourceService.deletePostResources(POST_ID);

        verify(resourceValidator, times(1)).validatePostExists(POST_ID);
        verify(s3Service, never()).deleteFile(anyString());
        verify(resourceRepository, never()).deleteByPostId(POST_ID);
    }

    @Test
    void deleteResources_Success() {
        List<Long> resourceIds = Arrays.asList(1L, 2L);
        List<Resource> resources = Arrays.asList(
                createResource(1L, "key1", "img1.jpg"),
                createResource(2L, "key2", "img2.jpg")
        );

        when(resourceRepository.findAllById(resourceIds)).thenReturn(resources);

        resourceService.deleteResources(POST_ID, resourceIds);

        verify(resourceRepository, times(1)).findAllById(resourceIds);
        verify(s3Service, times(1)).deleteFile("key1");
        verify(s3Service, times(1)).deleteFile("key2");
        verify(resourceRepository, times(1)).deleteAllById(resourceIds);
    }

    @Test
    void deleteResources_ResourceNotBelongsToPost() {
        List<Long> resourceIds = List.of(1L);
        Resource resource = createResource(1L, "key1", "img1.jpg");
        resource.setPostId(999L);

        when(resourceRepository.findAllById(resourceIds)).thenReturn(List.of(resource));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> resourceService.deleteResources(POST_ID, resourceIds));

        assertTrue(exception.getMessage().contains("does not belong to post"));
        verify(s3Service, never()).deleteFile(anyString());
        verify(resourceRepository, never()).deleteAllById(anyList());
    }

    @Test
    void deleteResources_EmptyResourceIds() {
        resourceService.deleteResources(POST_ID, null);
        resourceService.deleteResources(POST_ID, List.of());

        verify(resourceRepository, never()).findAllById(anyList());
        verify(s3Service, never()).deleteFile(anyString());
        verify(resourceRepository, never()).deleteAllById(anyList());
    }

    private void setupCommonValidatorMocks(List<MultipartFile> files) {
        doNothing().when(resourceValidator).validatePostExists(POST_ID);
        doNothing().when(resourceValidator).validateFiles(files);
        doNothing().when(resourceValidator).validateFileLimit(POST_ID, files.size());
    }
}
