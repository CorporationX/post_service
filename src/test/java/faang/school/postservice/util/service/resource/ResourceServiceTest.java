package faang.school.postservice.util.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.S3Service;
import faang.school.postservice.service.resource.ResourceServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    private static final Long POST_ID = 1L;
    private static final Long RESOURCE_ID = 1L;
    private static final Long NON_EXISTENT_RESOURCE_ID = 600L;

    private static final String CONTENT = "Test image content";
    private static final String FILE_NAME = "test.jpg";
    private static final String FILE_KEY = "posts/images/test-uuid.jpg";

    private static final long VALID_FILE_SIZE = 1024L;
    private static final long LARGE_FILE_SIZE = 6L * 1024 * 1024;
    private static final int MAX_FILES_PER_POST = 10;
    private static final int TOO_MANY_FILES = 11;

    private Post testPost;
    private Resource testResource;

    @Mock
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private S3Service s3Service;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @BeforeEach
    void setUp() {
        testPost = Post.builder().id(POST_ID).resources(new ArrayList<>()).build();
        testResource = Resource.builder()
                .id(RESOURCE_ID)
                .key(FILE_KEY)
                .name(FILE_NAME)
                .size(VALID_FILE_SIZE)
                .type("IMAGE")
                .post(testPost)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        ReflectionTestUtils.setField(resourceService, "maxFileSizeMb", 5);
        ReflectionTestUtils.setField(resourceService, "maxImagesPerPost", 10);
        ReflectionTestUtils.setField(resourceService, "entityManager", entityManager);
    }

    @Test
    void uploadResources_WithValidFilesAndExistingPostShouldUploadSuccessfully() {
        List<MultipartFile> files = List.of(
                createMultipartFile("image1.jpg", MediaType.IMAGE_JPEG_VALUE, CONTENT.getBytes()),
                createMultipartFile("image2.png", MediaType.IMAGE_PNG_VALUE, CONTENT.getBytes())
        );

        when(postService.getPostEntityById(POST_ID)).thenReturn(testPost);

        doNothing().when(entityManager).persist(any(Resource.class));

        List<ResourceDto> expectedDtos = List.of(
                createResourceDto(1L),
                createResourceDto(2L)
        );
        when(resourceMapper.toDtoList(anyList())).thenReturn(expectedDtos);

        List<ResourceDto> result = assertDoesNotThrow(() ->
                resourceService.uploadResources(POST_ID, files)
        );

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(postService).getPostEntityById(POST_ID);
        verify(s3Service, times(2)).uploadFile(any(), any(), any());
        verify(postRepository).save(testPost);
        verify(entityManager, times(2)).persist(any(Resource.class));
    }

    @Test
    void uploadResources_WithEmptyFilesListShouldThrowDataValidationException() {
        List<MultipartFile> emptyFiles = new ArrayList<>();

        assertThrows(DataValidationException.class,
                () -> resourceService.uploadResources(POST_ID, emptyFiles));

        verifyNoInteractions(postService, s3Service, postRepository);
    }

    @Test
    void uploadResources_WithMoreThanTenFiles_ShouldThrowDataValidationException() {
        List<MultipartFile> tooManyFiles = new ArrayList<>();
        for (int i = 0; i < TOO_MANY_FILES; i++) {
            tooManyFiles.add(createMultipartFile("image" + i + ".jpg", MediaType.IMAGE_JPEG_VALUE,
                    CONTENT.getBytes()));
        }

        assertThrows(DataValidationException.class,
                () -> resourceService.uploadResources(POST_ID, tooManyFiles));

        verifyNoInteractions(postService, s3Service, postRepository);
    }

    @Test
    void uploadResources_WithFileSizeExceedingLimitShouldThrowDataValidationException() {
        byte[] largeFileContent = new byte[(int) LARGE_FILE_SIZE];
        List<MultipartFile> files = List.of(
                createMultipartFile("large.jpg", MediaType.IMAGE_JPEG_VALUE, largeFileContent)
        );

        assertThrows(DataValidationException.class,
                () -> resourceService.uploadResources(POST_ID, files));

        verifyNoInteractions(postService, s3Service, postRepository);
    }

    @Test
    void uploadResources_WithNonImageFileTypeShouldThrowDataValidationException() {
        List<MultipartFile> files =List.of(
                createMultipartFile("document.pdf", MediaType.APPLICATION_PDF_VALUE, CONTENT.getBytes())
        );

        assertThrows(DataValidationException.class,
                () -> resourceService.uploadResources(POST_ID, files));

        verifyNoInteractions(postService, s3Service, postRepository);
    }

    @Test
    void uploadResources_WhenPostAlreadyHasTenImagesShouldThrowDataValidationException() {
        List<MultipartFile> files = List.of(
                createMultipartFile("image1.jpg", MediaType.IMAGE_JPEG_VALUE, CONTENT.getBytes())
        );

        for (int i = 0; i < MAX_FILES_PER_POST; i++) {
            testPost.getResources().add(Resource.builder()
                    .id((long) i)
                    .type("IMAGE")
                    .build());
        }

        when(postService.getPostEntityById(POST_ID)).thenReturn(testPost);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> resourceService.uploadResources(POST_ID, files));

        assertTrue(exception.getMessage().contains("Cannot upload 1 images. Post already has 10 images. Maximum 10 images allowed per post."));

        verify(postService).getPostEntityById(POST_ID);
        verifyNoInteractions(s3Service);
        verifyNoInteractions(entityManager);
        verify(postRepository, never()).save(any());
    }

    @Test
    void uploadResources_WithIOExceptionDuringFileProcessingShouldThrowDataValidationException() throws IOException {
        MultipartFile problematicFile = mock(MultipartFile.class);
        when(problematicFile.isEmpty()).thenReturn(false);
        when(problematicFile.getSize()).thenReturn(VALID_FILE_SIZE);
        when(problematicFile.getContentType()).thenReturn(MediaType.IMAGE_JPEG_VALUE);
        when(problematicFile.getOriginalFilename()).thenReturn(FILE_NAME);
        when(problematicFile.getBytes()).thenThrow(new IOException("File read error"));

        List<MultipartFile> files = List.of(problematicFile);
        when(postService.getPostEntityById(POST_ID)).thenReturn(testPost);

        assertThrows(DataValidationException.class,
                () -> resourceService.uploadResources(POST_ID, files));

        verify(postService).getPostEntityById(POST_ID);
        verify(s3Service, never()).uploadFile(any(), any(), any());
        verify(entityManager, never()).persist(any(Resource.class));
    }

    @Test
    void uploadResources_WithNullFilesListShouldThrowDataValidationException() {
        assertThrows(DataValidationException.class,
                () -> resourceService.uploadResources(POST_ID, null));

        verifyNoInteractions(postService, s3Service, postRepository);
    }

    @Test
    void getResourcesByPostId_WithExistingPostShouldReturnImageResources() {
        testPost.getResources().add(testResource);

        List<Resource> imageResources = List.of(testResource);
        List<ResourceDto> expectedDtos = List.of(createResourceDto(RESOURCE_ID));

        when(postService.getPostEntityById(POST_ID)).thenReturn(testPost);
        doReturn(expectedDtos).when(resourceMapper).toDtoList(imageResources);

        List<ResourceDto> result = resourceService.getResourcesByPostId(POST_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postService).getPostEntityById(POST_ID);
        verify(resourceMapper).toDtoList(imageResources);
    }

    @Test
    void getResourcesByPostId_WithMixedResourceTypesShouldReturnOnlyImages() {
        Resource imageResource = Resource.builder().id(1L).type("IMAGE").build();
        Resource videoResource = Resource.builder().id(2L).type("VIDEO").build();
        testPost.getResources().add(imageResource);
        testPost.getResources().add(videoResource);

        List<Resource> imageResources = List.of(imageResource);
        List<ResourceDto> expectedDtos = List.of(createResourceDto(1L));

        when(postService.getPostEntityById(POST_ID)).thenReturn(testPost);
        doReturn(expectedDtos).when(resourceMapper).toDtoList(imageResources);

        List<ResourceDto> result = resourceService.getResourcesByPostId(POST_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(resourceMapper).toDtoList(imageResources);
    }

    @Test
    void getResourcesByPostId_WithNoImages_ShouldReturnEmptyList() {
        when(postService.getPostEntityById(POST_ID)).thenReturn(testPost);
        when(resourceMapper.toDtoList(List.of())).thenReturn(List.of());

        List<ResourceDto> result = resourceService.getResourcesByPostId(POST_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postService).getPostEntityById(POST_ID);
        verify(resourceMapper).toDtoList(List.of());
    }

    @Test
    void deleteResource_WithExistingResourceShouldDeleteFromStorageAndDatabase() {
        testPost.getResources().add(testResource);
        when(entityManager.find(Resource.class, RESOURCE_ID)).thenReturn(testResource);

        when(entityManager.contains(testResource)).thenReturn(true);
        doNothing().when(entityManager).remove(any(Resource.class));

        resourceService.deleteResource(RESOURCE_ID);

        verify(entityManager).find(Resource.class, RESOURCE_ID);
        verify(s3Service).deleteFile(FILE_KEY);
        verify(postRepository).save(testPost);
        assertFalse(testPost.getResources().contains(testResource));
    }

    @Test
    void deleteResource_WithNonExistentResourceShouldThrowResourceNotFoundException() {
        when(entityManager.find(Resource.class, NON_EXISTENT_RESOURCE_ID)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> resourceService.deleteResource(NON_EXISTENT_RESOURCE_ID));

        verify(entityManager).find(Resource.class, NON_EXISTENT_RESOURCE_ID);
        verifyNoInteractions(s3Service, postRepository);
    }

    @Test
    void downloadResource_WithExistingResourceShouldReturnFileWithCorrectHeaders() {
        byte[] fileContent = CONTENT.getBytes();
        when(entityManager.find(Resource.class, RESOURCE_ID)).thenReturn(testResource);
        when(s3Service.downloadFile(FILE_KEY)).thenReturn(fileContent);

        ResponseEntity<byte[]> result = resourceService.downloadResource(RESOURCE_ID);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertArrayEquals(fileContent, result.getBody());
        assertEquals(MediaType.IMAGE_JPEG, result.getHeaders().getContentType());
        assertEquals(FILE_NAME, result.getHeaders().getContentDisposition().getFilename());

        verify(entityManager).find(Resource.class, RESOURCE_ID);
        verify(s3Service).downloadFile(FILE_KEY);
    }

    @Test
    void downloadResource_WithNonExistentResourceShouldThrowResourceNotFoundException() {
        when(entityManager.find(Resource.class, NON_EXISTENT_RESOURCE_ID)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> resourceService.downloadResource(NON_EXISTENT_RESOURCE_ID));

        verify(entityManager).find(Resource.class, NON_EXISTENT_RESOURCE_ID);
        verifyNoInteractions(s3Service);
    }

    private MultipartFile createMultipartFile(String filename, String contentType, byte[] content) {
        return new MockMultipartFile("file", filename, contentType, content);
    }

    private ResourceDto createResourceDto(Long id) {
        return new ResourceDto(
                id,
                "key-" + id,
                VALID_FILE_SIZE,
                "file-" + id + ".jpg",
                "IMAGE",
                LocalDateTime.now(),
                POST_ID
        );
    }
}

