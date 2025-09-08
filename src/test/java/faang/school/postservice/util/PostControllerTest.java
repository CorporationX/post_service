package faang.school.postservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.PostController;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.integration.project.config.ProjectClientProperties;
import faang.school.postservice.integration.project.service.ProjectClient;
import faang.school.postservice.integration.user.config.UserClientProperties;
import faang.school.postservice.integration.user.service.UserClient;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.net.URL;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private UserContext userContext;

    @MockBean
    private ProjectClient projectClient;

    @MockBean
    private UserClient userClient;

    @Test
    public void testMarkedPostAsDeleted_success() throws Exception {
        long postId = 1;
        PostDto postDto1 = objectMapper.readValue(new File(getResource("static/deletedPosts.json").toURI()),
                PostDto.class);
        BDDMockito.given(postService.markPostAsDeleted(postId)).willReturn(postDto1);
        MvcResult mvcResult = mockMvc.perform(put("/api/v1/post/markedPostAsDeleted/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        PostDto postDto = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), PostDto.class);
        Assertions.assertTrue(postDto.isDeleted());
        Assertions.assertFalse(postDto.isPublished());
    }

    @Test
    public void testUpdatePost_success() throws Exception {
        long postId = 1;
        PostDto postDto = objectMapper.readValue(new File(getResource("static/updatePost.json").toURI()),
                PostDto.class);
        BDDMockito.given(postService.updatePost(postDto, postId)).willReturn(postDto);
        MvcResult mvcResult = mockMvc.perform(put("/api/v1/post/update/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto))
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        PostDto postDto1 = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), PostDto.class);
        Assertions.assertEquals(postDto.getContent(), postDto1.getContent());
    }

    @Test
    public void testPublishPost_success() throws Exception {
        mockMvc.perform(put("/api/v1/post/publishPost")
                .queryParam("postId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        Mockito.verify(postService, Mockito.times(1)).publishPost(Mockito.any(), Mockito.any());
    }

    private URL getResource(String fileName) {
        return this.getClass().getClassLoader().getResource(fileName);
    }
}
