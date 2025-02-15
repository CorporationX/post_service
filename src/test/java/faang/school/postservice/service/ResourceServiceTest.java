package faang.school.postservice.service;

import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    private Resource resource;

    @BeforeEach
    void setUp() {
        resource = Resource.builder()
                .id(1L)
                .key("test-key")
                .build();
    }

    @Test
    void saveResource_shouldSaveResourceSuccessfully() {
        when(resourceRepository.save(resource)).thenReturn(resource);

        resourceService.saveResource(resource);

        verify(resourceRepository, times(1)).save(resource);
    }

    @Test
    void deleteResource_shouldDeleteResourceSuccessfully() {
        doNothing().when(resourceRepository).deleteById(resource.getId());

        resourceService.deleteResource(resource.getId());

        verify(resourceRepository, times(1)).deleteById(resource.getId());
    }
}
