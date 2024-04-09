package faang.school.postservice.controller;

import faang.school.postservice.controller.ResourceController;
import faang.school.postservice.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {
    @InjectMocks
    private ResourceController resourceController;
    @Mock
    private ResourceService resourceService;
    @Mock
    private MultipartFile file;
    long postId = 1L;
    long imageId = 5L;
    private List<MultipartFile> files;
    @Test
    void addResourceSuccessful() {
        resourceController.addResource(postId, files);
        Mockito.verify(resourceService).addResource(postId, files);
    }

    @Test
    void deleteResourceSuccessful() {
        resourceController.deleteResource(postId, imageId);
        Mockito.verify(resourceService).deleteResource(postId, imageId);
    }
}