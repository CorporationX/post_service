package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    private final static int MAX_FILES_AMOUNT = 10;
    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private ResourceRepository resourceRepository;
    @Spy
    private ResourceMapper resourceMapper;
    @Mock
    private PostValidator postValidator;
    @InjectMocks
    private ResourceService resourceService;

    private Post post;
    private List<Resource> resources;
    private List<Long> resourceIds;
    private final String key = "key";

    @BeforeEach
    void init() {
        post = Post.builder().authorId(1L).projectId(null).build();
        Resource resource1 = Resource.builder()
                .id(1L)
                .key(key)
                .post(post)
                .build();
        Resource resource2 = Resource.builder()
                .id(2L)
                .key(key)
                .post(post)
                .build();
        Resource resource3 = Resource.builder()
                .id(3L)
                .key(key)
                .post(post)
                .build();
        resources = new ArrayList<>(List.of(resource1, resource2, resource3));
        post.setResources(resources);

        resourceIds = resources.stream().map(Resource::getId).toList();

        setMaxAmountFiles();
    }

    @Test
    void testDeleteResources() {
        List<ResourceDto> expectedResourceDtos = resources.stream().map(resourceMapper::toDto).toList();
        resourceIds.forEach(this::mockFindById);

        List<ResourceDto> resourceDtos = resourceService.deleteResources(resourceIds);

        verify(amazonS3Service, times(resourceIds.size())).deleteFile(key);
        verify(resourceRepository, times(1)).deleteAll(resources);
        assertEquals(expectedResourceDtos, resourceDtos);
    }

    @Test
    void testGetResourceById_resourceExists_returnsResource() {
        long id = 1L;
        Resource expectedResource = mockFindById(id);

        Resource resourceById = resourceService.getResourceById(id);

        assertEquals(expectedResource, resourceById);
    }

    @Test
    void testGetResourceById_resourceNotExists_throwsEntityNotFoundException() {
        long id = 1000L;
        mockFindById(id);

        assertThrows(
                EntityNotFoundException.class,
                () -> resourceService.getResourceById(id)
        );
    }

    @Test
    void testGetResource_resourceExists_returnsResourceDto() {
        long id = 1L;
        Resource resource = mockFindById(id);

        ResourceDto expectedResourceDto = resourceMapper.toDto(resource);

        ResourceDto resourceDtoByService = resourceService.getResource(id);

        verify(resourceRepository, times(1)).findById(id);
        assertEquals(expectedResourceDto, resourceDtoByService);
    }

    @Test
    void testGetResource_resourceNotExists_throwsEntityNotFoundException() {
        long id = 1000L;
        mockFindById(id);

        assertThrows(
                EntityNotFoundException.class,
                () -> resourceService.getResource(id)
        );

        verify(resourceRepository, times(1)).findById(id);
    }

    @Test
    void testCreateResources_moreThenTenFiles_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> resourceService.createResources(post, mockFiles(false))
        );
    }

    @Test
    void testCreateResources() {
        List<MultipartFile> files = mockFiles(true);

        files.forEach(file ->
                when(amazonS3Service.uploadFile(any(), anyString())).thenReturn(new Resource())
        );

        List<ResourceDto> resourceDtos = resourceService.createResources(post, files);

        verify(resourceRepository, times(1)).saveAll(any());
        resourceDtos.forEach( resourceDto ->
                assertEquals(post.getId(), resourceDto.getPostId())
        );
    }

    @Test
    void TestDownloadResource () throws IOException {
        long id = 1L;
        byte[] expectedBytes = {1, 2, 3, 4};
        Resource resource = mockFindById(id);
        InputStream inputStream = mock(InputStream.class);
        String key = resource.getKey();
        when(amazonS3Service.downloadFile(key)).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn(expectedBytes);

        byte[] bytes = resourceService.downloadResource(id);

        verify(amazonS3Service, times(1)).downloadFile(key);
        verify(inputStream, times(1)).readAllBytes();
        assertEquals(expectedBytes, bytes);
    }

    private Resource mockFindById(Long id) {
        if (!resourceIds.contains(id)) {
            when(resourceRepository.findById(id)).thenReturn(Optional.empty());
            return null;
        }
        Resource resource = resources.get(id.intValue() - 1);
        when(resourceRepository.findById(id)).thenReturn(Optional.of(resource));
        return resource;
    }

    private List<MultipartFile> mockFiles(boolean validAmount) {
        int filesAmount = MAX_FILES_AMOUNT - post.getResources().size(); //always 10 files
        if (!validAmount) {
            filesAmount += 2; //to have more than 10 files
        }

        List<MultipartFile> files = new ArrayList<>();
        MultipartFile file = mock(MultipartFile.class);

        IntStream.range(0, filesAmount).forEach((i) -> files.add(file));
        return files;
    }

    private void setMaxAmountFiles() {
        try {
            Field field = ResourceService.class.getDeclaredField("maxAmountFiles");
            field.setAccessible(true);
            field.set(resourceService, ResourceServiceTest.MAX_FILES_AMOUNT);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}