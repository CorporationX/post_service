package faang.school.postservice.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static faang.school.postservice.controller.post.PostControllerTestData.buildCreateDto;
import static faang.school.postservice.controller.post.PostControllerTestData.buildPost;
import static faang.school.postservice.controller.post.PostControllerTestData.toViewDto;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService service;
    @MockBean
    private UserContext userContext;
    @Autowired
    private ObjectMapper objMapper;

    @Test
    void create_success() throws Exception {
        var createDto = buildCreateDto(1L, null);
        var post = buildPost(1L, 1L, null, createDto.content());
        var postDto = toViewDto(post);
        when(service.create(eq(createDto))).thenReturn(postDto);
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(createDto)))
                .andExpect(content().json(objMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk());
    }

    @Test
    void publish_success() throws Exception {
        var postId = 1L;
        mockMvc.perform(post("/posts/" + postId + "/publish"))
                .andExpect(status().isOk());
        verify(service).publish(postId);
    }

    @Test
    void update_success() throws Exception {
        var postId = 1L;
        var updateDto = new PostUpdateDto("New content");
        var post = buildPost(postId, 1L, null, updateDto.content());
        var postDto = toViewDto(post);
        when(service.update(eq(postId), eq(updateDto)))
                .thenReturn(postDto);
        mockMvc.perform(put("/posts/" + postId)
                        .content(objMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_success() throws Exception {
        var postId = 1L;
        mockMvc.perform(delete("/posts/" + postId))
                .andExpect(status().isOk());
        verify(service).delete(postId);
    }

    @Test
    void getById_success() throws Exception {
        var postId = 1L;
        var post = buildPost(postId, 1L, null, "content");
        var postDto = toViewDto(post);
        when(service.getById(postId)).thenReturn(postDto);
        mockMvc.perform(get("/posts/" + postId))
                .andExpect(content().json(objMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getList() throws Exception {
        var post1 = buildPost(1L, 2L, null, "content");
        var post2 = buildPost(2L, 2L, null, "content2");
        var filterDto = new PostFilterDto(2L, true, false);
        var postList = List.of(toViewDto(post1), toViewDto(post2));
        when(service.getList(refEq(filterDto))).thenReturn(postList);
        mockMvc.perform(get("/posts/search")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("authorId", "2")
                        .param("authorIsUser", "true")
                        .param("published", "false"))
                .andExpect(content().json(objMapper.writeValueAsString(postList)))
                .andExpect(status().isOk());
    }
}