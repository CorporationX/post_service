package faang.school.postservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    private Resource mockResource;

    @BeforeEach
    void setUp() {
        mockResource = new Resource();
        mockResource.setKey("test-key");
    }

    @Test
    void testCreateResource() {
        when(resourceRepository.save(any(Resource.class))).thenReturn(mockResource);

        Resource createdResource = resourceService.createResource(mockResource);

        assertNotNull(createdResource);
        assertEquals("test-key", createdResource.getKey());
        verify(resourceRepository, times(1)).save(mockResource);
    }

    @Test
    void testFindResourceByKey() {
        when(resourceRepository.findByKey("test-key")).thenReturn(Optional.of(mockResource));

        Resource foundResource = resourceService.findResourceByKey("test-key");

        assertNotNull(foundResource);
        assertEquals("test-key", foundResource.getKey());
        verify(resourceRepository, times(1)).findByKey("test-key");
    }

    @Test
    void testFindResourceByKey_throwsResourceNotFoundException() {
        when(resourceRepository.findByKey("invalid-key")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                resourceService.findResourceByKey("invalid-key"));
    }

    @Test
    void testDeleteResourceByKey() {
        doNothing().when(resourceRepository).deleteResourceByKey("test-key");

        resourceService.deleteResourceByKey("test-key");

        verify(resourceRepository, times(1)).deleteResourceByKey("test-key");
    }
}

