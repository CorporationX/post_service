package faang.school.postservice.util.service.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.PostController;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {PostController.class, PostService.class})
@WebMvcTest
public class PostControllerTest {
    private final String REQUEST_URL = "/posts";
    private final String PUBLISH_URL = REQUEST_URL + "/publish/{postId}";
    private final String DRAFTS_BY_AUTHOR_URL = REQUEST_URL + "/drafts/author/{authorId}";
    private final String DRAFTS_BY_PROJECT_URL = REQUEST_URL + "/drafts/project/{projectId}";
    private final String PUBLISHED_BY_AUTHOR_URL = REQUEST_URL + "/published/author/{authorId}";
    private final String PUBLISHED_BY_PROJECT_URL = REQUEST_URL + "/published/project/{projectId}";
    private final String REQUEST_URL_POST_ID = REQUEST_URL + "/{postId}";

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @MockBean
    private PostService postService;
    @MockBean
    private  UserContext context;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPositiveCreatedPost() throws Exception {
        when(postService.create(any())).thenReturn(preparePostDto());

        mockMvc.perform(post(REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(preparePostDto())))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(preparePostDto())))
                .andExpect(status().isOk());
    }

    @Test
    void testPositivePublish() throws Exception {
        when(postService.publish(any())).thenReturn(preparePostDto());

        mockMvc.perform(put(PUBLISH_URL, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(preparePostDto())))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(preparePostDto())))
                .andExpect(status().isOk());
    }

    @Test
    void testPositiveUpdatePost() throws Exception {
        when(postService.update(any(), any())).thenReturn(preparePostDto());

        mockMvc.perform(put(REQUEST_URL_POST_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(preparePostDto())))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(preparePostDto())))
                .andExpect(status().isOk());
    }

    @Test
    void testPositiveDeletePost() throws Exception {

        mockMvc.perform(delete(REQUEST_URL_POST_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(preparePostDto())))
                .andExpect(status().isOk());
    }

    @Test
    void testPositiveGetPost() throws Exception {
        when(postService.getPost(1L,1L)).thenReturn(preparePostDto());
        when(context.getUserId()).thenReturn(1L);

        mockMvc.perform(get(REQUEST_URL_POST_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(preparePostDto())))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(preparePostDto())))
                .andExpect(status().isOk());
    }

    @Test
    void testPositiveGetDraftsByAuthor() throws Exception {
        when(postService.findDraftsByAuthorId(any(),any())).thenReturn(list());
        when(context.getUserId()).thenReturn(1L);

        mockMvc.perform(get(DRAFTS_BY_AUTHOR_URL, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(list())))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(list())))
                .andExpect(status().isOk());
    }

    @Test
    void testPositiveGetDraftsByProject() throws Exception {
        when(postService.findDraftsByProjectId(any(),any())).thenReturn(list());
        when(context.getUserId()).thenReturn(1L);

        mockMvc.perform(get(DRAFTS_BY_PROJECT_URL, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(list())))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(list())))
                .andExpect(status().isOk());
    }

    @Test
    void testPositiveGetPublishedByAuthor() throws Exception {
        when(postService.findPublishedByAuthorId(any(),any())).thenReturn(list());
        when(context.getUserId()).thenReturn(1L);

        mockMvc.perform(get(PUBLISHED_BY_AUTHOR_URL, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(list())))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(list())))
                .andExpect(status().isOk());
    }

    @Test
    void testPositiveGetPublishedByProject() throws Exception {
        when(postService.findPublishedByProjectId(any(),any())).thenReturn(list());
        when(context.getUserId()).thenReturn(1L);

        mockMvc.perform(get(PUBLISHED_BY_PROJECT_URL, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(list())))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(list())))
                .andExpect(status().isOk());
    }

    private PostDto preparePostDto() {
        return PostDto.builder()
                .id(1L)
                .authorId(1L)
                .content("content")
                .build();
    }

    private List<PostDto> list() {
        return List.of(preparePostDto());
    }
}
