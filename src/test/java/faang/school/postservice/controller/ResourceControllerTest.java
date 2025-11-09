package faang.school.postservice.controller;

import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.service.resource.ResourceServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static faang.school.postservice.util.TestDataBuilder.DTO;
import static faang.school.postservice.util.TestDataBuilder.FILE;
import static faang.school.postservice.util.TestDataBuilder.POST_ID;
import static faang.school.postservice.util.TestDataBuilder.RESOURCE;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceServiceImpl resourceService;

    @MockBean
    private ResourceMapper resourceMapper;


    @Test
    void uploadResources_ReturnsResourceListWhenValidPostIdAndFiles() throws Exception {


        when(resourceService.uploadResourcesForPost(anyList(), eq(POST_ID))).thenReturn(List.of(RESOURCE));
        when(resourceMapper.toResourceDto(RESOURCE)).thenReturn(DTO);

        mockMvc.perform(multipart("/posts/{postId}/resources", POST_ID)
                        .file("files", FILE.getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(DTO.id()))
                .andExpect(jsonPath("$[0].name").value(DTO.name()))
                .andExpect(jsonPath("$[0].size").value(DTO.size()));

        verify(resourceService).uploadResourcesForPost(anyList(), eq(POST_ID));
    }

    @Test
    void updateResources_ReturnsOkWhenValidParameters() throws Exception {
        MultipartFile newFile = mock(MultipartFile.class);
        List<Long> filesToDelete = List.of(1L, 2L);

        mockMvc.perform(multipart("/posts/{postId}/resources", POST_ID)
                        .file("newFiles", newFile.getBytes())
                        .param("filesToDelete", "1", "2")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(resourceService).updatePostResources(eq(POST_ID), anyList(), eq(filesToDelete));
    }

    @Test
    void getResources_ReturnsResourceListWhenValidPostId() throws Exception {

        when(resourceService.getResourcesByPostId(POST_ID)).thenReturn(List.of(RESOURCE));
        when(resourceMapper.toResourceDto(RESOURCE)).thenReturn(DTO);

        mockMvc.perform(get("/posts/{postId}/resources", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(resourceService).getResourcesByPostId(POST_ID);
    }

    @Test
    void deleteResources_ReturnsOkWhenValidPostId() throws Exception {
        mockMvc.perform(delete("/posts/{postId}/resources", POST_ID))
                .andExpect(status().isOk());

        verify(resourceService).deletePostResources(POST_ID);
    }
}
