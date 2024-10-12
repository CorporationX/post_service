package faang.school.postservice.controller;

import faang.school.postservice.service.resource.ResourceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ResourceControllerTest {

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private ResourceController resourceController;

    private final long id = 1;

    @Test
    void testAddResource() {
        MultipartFile multipartFile = mock(MultipartFile.class);

        resourceController.addResource(id, multipartFile);

        verify(resourceService).addResource(id, multipartFile);
    }

    @Test
    void testDownloadResource() {
        ResponseEntity<byte[]> responseEntity = mock(ResponseEntity.class);

        when(resourceService.downloadResource(id)).thenReturn(responseEntity);

        assertDoesNotThrow(() -> resourceController.downloadImageResource(id));
    }

    @Test
    void testDeleteResource() {
        resourceController.deleteResource(id);

        verify(resourceService, times(1)).deleteResource(id);
    }
}