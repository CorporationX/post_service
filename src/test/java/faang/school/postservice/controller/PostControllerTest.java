package faang.school.postservice.controller;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.mapper.ResourceMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ResourceEntity;
import faang.school.postservice.model.ResourceType;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.resource.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    private final PostService postService = Mockito.mock(PostService.class);
    private final PostMapper postMapper = new PostMapperImpl();
    private final ResourceService resourceService = Mockito.mock(ResourceService.class);
    private final ResourceMapper resourceMapper = new ResourceMapperImpl();

    private final PostController postController =
            new PostController(postService, postMapper, resourceService, resourceMapper);

    private MockMvc mockMvc;
    private MockMultipartFile mockFile;

    private Long postId;
    private ResourceEntity resourceEntityOne;
    private Long resourceOneId;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();


        mockFile = new MockMultipartFile(
                "file",
                "test_file.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                "test_content".getBytes()
        );

        postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .resourceEntities(new ArrayList<>() {{
                    add(resourceEntityOne);
                }})
                .build();

        resourceOneId = 10L;
        resourceEntityOne = ResourceEntity.builder()
                .id(resourceOneId)
                .key("res1")
                .type(ResourceType.IMAGE)
                .post(post)
                .build();
    }

    @Test
    @DisplayName("testAddFileToPost")
    public void testAddFileToPost() throws Exception {
        Mockito.when(resourceService.addFileToPost(mockFile, postId)).thenReturn(resourceEntityOne);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts/" + postId + "/files")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(postId))
                .andExpect(jsonPath("$.resourceId").value(resourceOneId))
                .andExpect(jsonPath("$.type").value("IMAGE"));
    }

    @Test
    @DisplayName("testUpdateFileToPost")
    public void testUpdateFileToPost() throws Exception {
        Mockito.when(resourceService.updateFileInPost(mockFile, resourceOneId)).thenReturn(resourceEntityOne);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts/files/update")
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            request.setParameter("resource-id", String.valueOf(resourceOneId));
                            return request;
                        }))
                .andExpect((status().isOk()))
                .andExpect(jsonPath("$.postId").value(postId))
                .andExpect(jsonPath("$.resourceId").value(resourceOneId))
                .andExpect(jsonPath("$.type").value("IMAGE"));
    }

    @Test
    @DisplayName("testRemoveFileInPost")
    public void testRemoveFileInPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/posts/files/remove")
                .param("resource-id", String.valueOf(resourceOneId)))
                .andExpect(status().isOk());
    }
}
