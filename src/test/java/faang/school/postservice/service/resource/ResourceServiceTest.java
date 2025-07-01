package faang.school.postservice.service.resource;

import faang.school.postservice.dto.image.ImageResource;
import faang.school.postservice.entity.resource.Resource;
import faang.school.postservice.model.resource.ImageResources;
import faang.school.postservice.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    private static final String FILE_KEY = "file-key";
    private static final String PREVIEW_KEY = "preview-key";
    private static final String FILE_NAME = "kik.jpg";
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final long SIZE = 123L;

    private Resource imageResource;
    private Resource previewResource;
    private ImageResource imageStorage;

    @BeforeEach
    void setUp() {
        imageStorage = new ImageResource(FILE_NAME, FILE_KEY, PREVIEW_KEY, CONTENT_TYPE, SIZE);

        imageResource = new Resource();
        imageResource.setId(1L);
        imageResource.setKey(FILE_KEY);
        imageResource.setName(FILE_NAME);
        imageResource.setType(CONTENT_TYPE);
        imageResource.setSize(SIZE);

        previewResource = new Resource();
        previewResource.setId(2L);
        previewResource.setKey(PREVIEW_KEY);
        previewResource.setName("preview_" + FILE_NAME);
        previewResource.setType(CONTENT_TYPE);
        previewResource.setSize(SIZE);
    }

    @Test
    void shouldSaveResource() {
        when(resourceRepository.save(imageResource)).thenReturn(imageResource);

        Resource result = resourceService.saveResource(imageResource);

        assertEquals(imageResource, result);
        verify(resourceRepository).save(imageResource);
    }

    @Test
    void shouldDeleteResource() {
        resourceService.deleteResource(imageResource);
        verify(resourceRepository).delete(imageResource);
    }

    @Test
    void shouldUploadImageResources() {
        when(resourceRepository.save(any(Resource.class)))
                .thenAnswer(invocation -> {
                    Resource r = invocation.getArgument(0);
                    if (r.getKey().equals(FILE_KEY)) {
                        r.setId(1L);
                    } else if (r.getKey().equals(PREVIEW_KEY)) {
                        r.setId(2L);
                    }
                    return r;
                });

        ImageResources result = resourceService.uploadImageResources(imageStorage);

        Resource original = result.original();
        assertEquals(FILE_KEY, original.getKey());
        assertEquals(FILE_NAME, original.getName());
        assertEquals(CONTENT_TYPE, original.getType());
        assertEquals(SIZE, original.getSize());

        Resource preview = result.preview();
        assertEquals(PREVIEW_KEY, preview.getKey());
        assertEquals("preview_" + FILE_NAME, preview.getName());
        assertEquals(CONTENT_TYPE, preview.getType());
        assertEquals(SIZE, preview.getSize());

        verify(resourceRepository, times(2)).save(any(Resource.class));
    }
}
