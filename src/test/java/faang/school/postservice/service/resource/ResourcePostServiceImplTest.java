package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.exceptions.EntityNotFoundException;
import faang.school.postservice.exceptions.FileException;
import faang.school.postservice.mapper.ResourcePostMapper;
import faang.school.postservice.model.ImageType;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.minio.MinioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourcePostServiceImplTest {

    @Mock
    private MinioServiceImpl minioService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ResourcePostMapper resourcePostMapper;
    @Mock
    private ImageProcessServiceImpl imageProcessServiceImpl;

    @InjectMocks
    private ResourcePostServiceImpl resourcePostService;

    @InjectMocks
    private ImageProcessServiceImpl imageProcessService;

    private Post testPost;
    private Resource testResource;
    private ResourceDto testResourceDto;

    @BeforeEach
    void setUp() throws Exception {
        testPost = new Post();
        testPost.setId(1L);
        testPost.setResources(new ArrayList<>());

        testResource = Resource.builder()
                .id(1L)
                .post(testPost)
                .key("test-key")
                .name("test-image.jpg")
                .type("image/jpeg")
                .size(1024L)
                .createdAt(LocalDateTime.now())
                .build();

        testResourceDto = ResourceDto.builder()
                .id(1L)
                .key("test-key")
                .name("test-image.jpg")
                .type("image/jpeg")
                .size(1024L)
                .build();
        setField(resourcePostService, "MAX_IMAGE_COUNT", 10);
        setField(resourcePostService, "MAX_SIZE_IMAGE", 5 * 1024 * 1024); // 5MB
        setField(resourcePostService, "formatImage", Arrays.asList(".jpg", ".jpeg", ".png"));

        setField(imageProcessService, "HORIZONTAL_MAX_WIDTH", 1920);
        setField(imageProcessService, "HORIZONTAL_MAX_HEIGHT", 1080);
        setField(imageProcessService, "SQUARE_MAX_WIDTH", 1080);
        setField(imageProcessService, "SQUARE_MAX_HEIGHT", 1080);
    }

    @Test
    void addResources_fileIsNull_shouldThrowIllegalArgumentException() {
        MultipartFile[] files = null;
        ImageType type = ImageType.HORIZONTAL;

        assertThrows(IllegalArgumentException.class,
                () -> resourcePostService.addResources(1L, files, type));
    }

    @Test
    void addResources_emptyFilesArray_shouldThrowIllegalArgumentException() {
        MultipartFile[] files = new MultipartFile[0];
        ImageType type = ImageType.HORIZONTAL;

        assertThrows(IllegalArgumentException.class,
                () -> resourcePostService.addResources(1L, files, type));
    }

    @Test
    void addResources_filesCountMoreMaxCount_shouldThrowIllegalArgumentException() {
        MultipartFile[] files = new MultipartFile[11];
        for (int i = 0; i < 11; i++) {
            files[i] = preparationDataMultipart();
        }
        ImageType type = ImageType.HORIZONTAL;

        assertThrows(IllegalArgumentException.class,
                () -> resourcePostService.addResources(1L, files, type));
    }

    @Test
    void addResources_postNotExist_shouldThrowEntityNotFoundException() {
        MultipartFile[] files = new MultipartFile[]{preparationDataMultipart()};
        ImageType type = ImageType.HORIZONTAL;

        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> resourcePostService.addResources(1L, files, type));
    }

    @Test
    void addResources_successfulUpload_shouldReturnResourceDtoList() {
        MultipartFile[] files = new MultipartFile[]{preparationDataMultipart()};
        ImageType type = ImageType.HORIZONTAL;

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(imageProcessServiceImpl.resizeImage(any(MultipartFile.class), any(ImageType.class)))
                .thenReturn("resized-image".getBytes());
        when(minioService.uploadImage(any(byte[].class), anyString(), anyString(), anyString()))
                .thenReturn(testResource);
        when(resourceRepository.save(any(Resource.class))).thenReturn(testResource);
        when(resourcePostMapper.toDto(testResource)).thenReturn(testResourceDto);

        List<ResourceDto> result = resourcePostService.addResources(1L, files, type);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testResourceDto, result.get(0));

        verify(postRepository).findById(1L);
        verify(imageProcessServiceImpl).resizeImage(any(MultipartFile.class), eq(type));
        verify(minioService).uploadImage(any(byte[].class), eq("posts/1"), anyString(), anyString());
        verify(resourceRepository).save(any(Resource.class));
        verify(resourcePostMapper).toDto(testResource);
    }

    @Test
    void addResources_multipleFiles_shouldProcessAllFiles() {
        MultipartFile[] files = new MultipartFile[]{
                preparationDataMultipart(),
                preparationDataMultipart()
        };
        ImageType type = ImageType.HORIZONTAL;

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(imageProcessServiceImpl.resizeImage(any(MultipartFile.class), any(ImageType.class)))
                .thenReturn("resized-image".getBytes());
        when(minioService.uploadImage(any(byte[].class), anyString(), anyString(), anyString()))
                .thenReturn(testResource);
        when(resourceRepository.save(any(Resource.class))).thenReturn(testResource);
        when(resourcePostMapper.toDto(testResource)).thenReturn(testResourceDto);

        List<ResourceDto> result = resourcePostService.addResources(1L, files, type);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(imageProcessServiceImpl, times(2)).resizeImage(any(MultipartFile.class), eq(type));
        verify(resourceRepository, times(2)).save(any(Resource.class));
    }

    @Test
    void addResources_ioExceptionDuringProcessing_shouldThrowFileException() {
        MultipartFile[] files = new MultipartFile[]{preparationDataMultipart()};
        ImageType type = ImageType.HORIZONTAL;

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(imageProcessServiceImpl.resizeImage(any(MultipartFile.class), any(ImageType.class)))
                .thenThrow(new FileException("Processing error"));

        assertThrows(FileException.class,
                () -> resourcePostService.addResources(1L, files, type));
    }

    @Test
    void getResource_postNotExist_shouldThrowEntityNotFoundException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> resourcePostService.getResource(1L));
    }

    @Test
    void getResource_successfulDownload_shouldReturnUrlList() {
        testPost.getResources().add(testResource);
        List<String> expectedUrls = Arrays.asList("url1", "url2");

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(minioService.downloadImage("test-key", "posts/1")).thenReturn(expectedUrls);

        List<String> result = resourcePostService.getResource(1L);

        assertNotNull(result);
        assertEquals(expectedUrls, result);
        verify(minioService).downloadImage("test-key", "posts/1");
    }

    @Test
    void deleteResource_postNotExist_shouldThrowEntityNotFoundException() {
        List<Long> resourceIds = Arrays.asList(1L, 2L);

        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> resourcePostService.deleteResource(1L, resourceIds));
    }

    @Test
    void deleteResource_successfulDeletion_shouldDeleteResources() {
        List<Long> resourceIds = Arrays.asList(1L, 2L);
        List<Resource> resourcesToDelete = Arrays.asList(testResource, testResource);
        testPost.getResources().addAll(resourcesToDelete);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(resourceRepository.findAllById(resourceIds)).thenReturn(resourcesToDelete);

        resourcePostService.deleteResource(1L, resourceIds);

        verify(minioService, times(2)).deleteImage("test-key");
        verify(resourceRepository).deleteAll(resourcesToDelete);
        assertTrue(testPost.getResources().isEmpty());
    }

    @Test
    void validateImageFile_maxImagesExceeded_shouldThrowDataValidationException() {
        for (int i = 0; i < 10; i++) {
            testPost.getResources().add(new Resource());
        }

        MultipartFile file = preparationDataMultipart();
        MultipartFile[] files = {file};

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        assertThrows(DataValidationException.class,
                () -> resourcePostService.addResources(1L, files, ImageType.HORIZONTAL));
    }

    @Test
    void validateImageFile_noFileExtension_shouldThrowIllegalArgumentException() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "testimage",
                "image/jpeg",
                "fake image content".getBytes()
        );
        MultipartFile[] files = {file};

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        assertThrows(IllegalArgumentException.class,
                () -> resourcePostService.addResources(1L, files, ImageType.HORIZONTAL));
    }

    @Test
    void validateImageFile_unsupportedFormat_shouldThrowIllegalArgumentException() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test-image.gif",
                "image/gif",
                "fake image content".getBytes()
        );
        MultipartFile[] files = {file};

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        assertThrows(IllegalArgumentException.class,
                () -> resourcePostService.addResources(1L, files, ImageType.HORIZONTAL));
    }


    private MultipartFile preparationDataMultipart() {
        return new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}