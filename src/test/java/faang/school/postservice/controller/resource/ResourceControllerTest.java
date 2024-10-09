package faang.school.postservice.controller.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.resource.ResourceStatus;
import faang.school.postservice.service.resource.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private ResourceDto resourceDto;
    private MockMultipartFile file;

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private ResourceController resourceController;

    @BeforeEach
    public void setUp() {
        resourceDto = new ResourceDto();
        resourceDto.setId(1L);
        resourceDto.setName("test-image.jpg");
        resourceDto.setType("image/jpeg");
        resourceDto.setSize("2 megabytes");
        resourceDto.setStatus(ResourceStatus.ACTIVE);

        file = new MockMultipartFile(
                "files",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test Image Content".getBytes()
        );

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(resourceController).build();
    }

    @Test
    @DisplayName("Should upload images successfully")
    public void testAttachImages_Success() throws Exception {
        List<ResourceDto> dtos = Collections.singletonList(resourceDto);

        when(resourceService.attachImages(eq(1L), anyList())).thenReturn(dtos);

        mockMvc.perform(multipart("/resources/posts/1/images")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));

        verify(resourceService).attachImages(eq(1L), anyList());
    }

    @Test
    @DisplayName("Should delete resource successfully")
    public void testDeleteResource_Success() throws Exception {
        when(resourceService.deleteResource(1L)).thenReturn(resourceDto);

        mockMvc.perform(delete("/resources/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resourceDto)));

        verify(resourceService).deleteResource(1L);
    }

    @Test
    @DisplayName("Should restore resource successfully")
    public void restoreResource_Success() throws Exception {
        when(resourceService.restoreResource(1L)).thenReturn(resourceDto);

        mockMvc.perform(put("/resources/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resourceDto)));

        verify(resourceService).restoreResource(1L);
    }
}
