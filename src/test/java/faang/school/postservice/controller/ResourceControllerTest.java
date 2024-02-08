package faang.school.postservice.controller;

import faang.school.postservice.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {
    @Mock
    private ResourceService resourceService;
    @InjectMocks
    private ResourceController resourceController;

    @Test
    void testAddResourceCallsResourceServiceMethod() {
        resourceController.addResource(1L, null);
        verify(resourceService, times(1)).addResource(1L, null);
    }

    @Test
    void testDeleteResourceCallsResourceServiceMethod() {
        resourceController.deleteResource(1L, 1L);
        verify(resourceService, times(1)).deleteResource(1L, 1L);
    }
}