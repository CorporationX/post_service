package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    private static final long VALID_AUTHOR_ID = 1L;
    private static final long VALID_POST_ID = 1L;
    private static final long VALID_PROJECT_ID = 1L;
    private static final long VALID_USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;
    @MockBean
    private UserContext userContext;

    private PostCreateDto postCreateDto;
    private PostViewDto postViewDto;
    private PostUpdateDto postUpdateDto;

    @BeforeEach
    public void setUp() {
        postCreateDto = new PostCreateDto();
        postViewDto = new PostViewDto();
        postUpdateDto = new PostUpdateDto();
    }

    @DisplayName("Проверка создания черновика поста с валидными данными")
    @Test
    public void givenValidPostCreateDtoWhenCreateDraftThenReturnPostViewDto() throws Exception {
        postCreateDto.setContent("content");
        postCreateDto.setAuthorId(VALID_AUTHOR_ID);

        postViewDto.setContent("content");
        postViewDto.setAuthorId(VALID_AUTHOR_ID);

        Mockito.when(postService.createDraft(postCreateDto)).thenReturn(postViewDto);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(postCreateDto.getAuthorId()))
                .andExpect(jsonPath("$.content").value(postViewDto.getContent()));

        Mockito.verify(postService, Mockito.times(1)).createDraft(postCreateDto);
    }

    @DisplayName("Проверка ошибки валидации при создании черновика с невалидными данными")
    @Test
    public void givenInvalidPostCreateDtoWhenCreateDraftThenReturnValidExceptions() throws Exception {
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Проверка публикации поста с валидным ID")
    @Test
    public void givenValidPostIdWhenPublishPostThenReturnPostViewDto() throws Exception {
        long postId = VALID_POST_ID;

        postViewDto.setContent("content");
        postViewDto.setAuthorId(VALID_AUTHOR_ID);

        Mockito.when(postService.publishPost(postId)).thenReturn(postViewDto);

        mockMvc.perform(put("/posts/{postId}/publish", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(postViewDto.getAuthorId()))
                .andExpect(jsonPath("$.content").value(postViewDto.getContent()));

        Mockito.verify(postService, Mockito.times(1)).publishPost(postId);
    }

    @DisplayName("Проверка обновления поста с валидными данными")
    @Test
    public void givenValidPostUpdateDtoWhenUpdatePostThenReturnPostViewDto() throws Exception {
        long postId = VALID_POST_ID;
        postUpdateDto.setContent("content");
        postViewDto.setContent("content");
        postViewDto.setAuthorId(VALID_AUTHOR_ID);

        Mockito.when(postService.updatePost(postUpdateDto, postId)).thenReturn(postViewDto);

        mockMvc.perform(put("/posts/{postId}/update", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdateDto)))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка мягкого удаления поста с валидным ID")
    @Test
    public void givenValidPostIdWhenSoftDeletePostThenReturnPostViewDto() throws Exception {
        long postId = VALID_POST_ID;
        postViewDto = new PostViewDto();
        postViewDto.setDeleted(true);

        Mockito.when(postService.softDeletePost(postId)).thenReturn(postViewDto);

        mockMvc.perform(put("/posts/{postId}/soft-delete", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка получения поста с валидным ID")
    @Test
    public void givenValidPostIdWhenGetPostThenReturnPostViewDto() throws Exception {
        long postId = VALID_POST_ID;

        postViewDto.setAuthorId(VALID_AUTHOR_ID);
        Mockito.when(postService.getPost(postId)).thenReturn(postViewDto);

        mockMvc.perform(get("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка получения черновиков пользователя с валидным ID")
    @Test
    public void givenValidPostIdWhenGetUserDraftsThenReturnListOfPostViewDto() throws Exception {
        long userId = VALID_USER_ID;

        Mockito.when(postService.getUserDrafts(userId))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/posts/user/{userId}/draft", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка получения черновиков проекта с валидным ID")
    @Test
    public void givenValidPostIdWhenGetProjectDraftsThenReturnListOfPostViewDto() throws Exception {
        long projectId = VALID_PROJECT_ID;

        Mockito.when(postService.getUserDrafts(projectId))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/posts/project/{projectId}/draft", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка получения опубликованных постов автора с валидным ID")
    @Test
    public void givenValidPostIdWhenGetAuthorPublishedPostsThenReturnListOfPostsViewDto() throws Exception {
        long userId = VALID_USER_ID;

        Mockito.when(postService.getUserDrafts(userId))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/posts/user/{userId}/published-post", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Проверка получения опубликованных постов проекта с валидным ID")
    @Test
    public void givenValidPostIdWhenGetProjectPublishedPostsThenReturnListOfPostsViewDto() throws Exception {
        long projectId = VALID_PROJECT_ID;

        Mockito.when(postService.getUserDrafts(projectId))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/posts/project/{projectId}/published-post", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}