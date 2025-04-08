package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceResponseDto;
import faang.school.postservice.exception.FileValidationException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3ServiceImpl;
import faang.school.postservice.service.thumbnails.ImageResizeImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    @Mock
    private S3ServiceImpl s3Service;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ImageResizeImpl imageResizeImpl;
    @Spy
    private ResourceMapper resourceMapper = Mappers.getMapper(ResourceMapper.class);
    @InjectMocks
    private ResourceServiceImpl resourceService;

    private final MultipartFile brokenImageMultipartFile = new MockMultipartFile("test.zip", new byte[]{});
    private final MultipartFile brokenNonImageMultipartFile = new MockMultipartFile("test.jpg", new byte[]{});

    @Test
    public void testUploadImageResourceWithInvalidFileName() {
        assertThrows(FileValidationException.class,
                () -> resourceService.uploadImageResource(1L, brokenImageMultipartFile));
    }

    @Test
    public void testUploadImageResourceWithInvalidFileExtension() {
        assertThrows(FileValidationException.class,
                () -> resourceService.uploadImageResource(1L, brokenImageMultipartFile));
    }

    @Test
    public void testUploadImageResource() {
        Long postId = 1L;
        long fileSize = 123L;
        String fileName = "test.jpg";
        String fileContentType = "image/jpeg";
        byte[] fileBytes = "test".getBytes();
        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, fileContentType, fileBytes);
        Resource resource = Resource.builder()
                .id(1L)
                .post(Post.builder().id(1L).build())
                .name(fileName)
                .size(fileSize)
                .key(postId + "/" + System.currentTimeMillis() + fileName)
                .type(fileContentType)
                .build();

        when(imageResizeImpl.getResizedImages(any())).thenReturn(Map.of());
        when(s3Service.uploadResource(any())).thenReturn(resource);
        when(resourceRepository.save(resource)).thenReturn(resource);

        ResponseEntity<List<ResourceResponseDto>> actualResult =
                resourceService.uploadImageResource(1L, multipartFile);

        verify(resourceMapper, times(1)).toResourceDto(resource);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.CREATED, actualResult.getStatusCode());

        List<ResourceResponseDto> resourceResponseDtos = actualResult.getBody();
        assertNotNull(resourceResponseDtos);
        assertEquals(1, resourceResponseDtos.size());
    }

    @Test
    public void testUploadResourceWithInvalidFileName() {
        assertThrows(FileValidationException.class,
                () -> resourceService.uploadResource(1L, brokenNonImageMultipartFile));
    }

    @Test
    public void testUploadResourceWithInvalidFileExtension() {
        assertThrows(FileValidationException.class,
                () -> resourceService.uploadResource(1L, brokenNonImageMultipartFile));
    }

    @Test
    public void testUploadResource() {
        Long postId = 1L;
        long fileSize = 123L;
        String fileName = "test.exe";
        String fileContentType = "application/x-msdos-program";
        byte[] fileBytes = "test".getBytes();
        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, fileContentType, fileBytes);
        Resource resource = Resource.builder()
                .id(1L)
                .post(Post.builder().id(1L).build())
                .name(fileName)
                .size(fileSize)
                .key(postId + "/" + System.currentTimeMillis() + fileName)
                .type(fileContentType)
                .build();

        when(s3Service.uploadResource(any())).thenReturn(resource);
        when(resourceRepository.save(resource)).thenReturn(resource);

        ResponseEntity<ResourceResponseDto> actualResult = resourceService.uploadResource(1L, multipartFile);

        verify(resourceMapper, times(1)).toResourceDto(resource);
        assertNotNull(actualResult);
        assertEquals(HttpStatus.CREATED, actualResult.getStatusCode());

        ResourceResponseDto resourceResponseDtos = actualResult.getBody();
        assertNotNull(resourceResponseDtos);
    }

    @Test
    public void testDownloadResourceWithInvalidResourceId() {
        assertThrows(EntityNotFoundException.class, () -> resourceService.downloadResource(1L));
    }

    @Test
    public void testDownloadResourceWithResourceWithoutKey() {
        when(resourceRepository.findResourceTypeById(1L)).thenReturn(Optional.of("image/jpeg"));

        assertThrows(EntityNotFoundException.class, () -> resourceService.downloadResource(1L));
    }

    @Test
    public void testDownloadResourceWithResource() {
        when(resourceRepository.findResourceTypeById(1L)).thenReturn(Optional.of("image/jpeg"));
        when(resourceRepository.findResourceKeyById(1L)).thenReturn(Optional.of("some key"));
        when(s3Service.downloadResource("some key"))
                .thenReturn(IOUtils.toInputStream("Hello", Charset.defaultCharset()));

        ResponseEntity<byte[]> actualResult = resourceService.downloadResource(1L);

        assertNotNull(actualResult);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, actualResult.getHeaders().getContentType());
    }

    @Test
    public void testDeleteResourceWithResourceWithoutKey() {
        assertThrows(EntityNotFoundException.class, () -> resourceService.deleteResource(1L));
    }

    @Test
    public void testDeleteResourceWithResource() {
        when(resourceRepository.findResourceKeyById(1L)).thenReturn(Optional.of("some key"));

        ResponseEntity<Void> actualResult = resourceService.deleteResource(1L);

        verify(s3Service, times(1)).deleteResource("some key");
        verify(resourceRepository, times(1)).deleteResourceById(1L);

        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }
}
