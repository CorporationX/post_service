package faang.school.postservice.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.service.post.PostServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PostControllerTest — тестирует REST-контроллер для постов {@link PostController}
 *
 * @author Linempy
 * @since 28.07.2025
 */
@WebMvcTest(PostController.class)
public class PostControllerTest {

    private static final String STRING_API_V1 = "/api/v1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objMapper;

    @MockBean
    private UserContext context;

    @MockBean
    private PostServiceImpl service;

    @Test
    @DisplayName("POST /api/v1/posts - должен вернуть статус 201 Created при успешном создании поста")
    public void shouldReturnCreatedStatus_WhenValidRequest() throws Exception {
        PostCreateDto createDto = new PostCreateDto("content", 1L, null);
        PostViewDto viewDto = new PostViewDto(
                createDto.content(), createDto.authorId(), createDto.projectId(), false, false, null
        );
        when(service.create(createDto)).thenReturn(viewDto);

        mockMvc.perform(post(STRING_API_V1 + "/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{postId}/publications - должен вернуть 204 No Content при успешной публикации")
    public void shouldReturnStatusNoContent_WhenValidRequest() throws Exception {
        Long postId = 1L;

        doNothing().when(service).publication(postId);

        mockMvc.perform(put(STRING_API_V1 + "/posts/{postId}/publications", postId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).publication(postId);
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{postId}/publications - должен вернуть 404 Not Found при отсутствии поста")
    public void shouldReturn404NotFound_WhenPostNotExist() throws Exception {
        Long postId = 999L;
        doThrow(new EntityNotFoundException("Post not found"))
                .when(service).publication(postId);

        mockMvc.perform(put(STRING_API_V1 + "/posts/{postId}/publications", postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found"));
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{postId} - должен вернуть обновленный пост со статусом 200 OK")
    public void shouldReturnUpdatedPost_WhenUpdateSuccessful() throws Exception {
        Long postId = 1L;
        PostUpdateDto updateDto = new PostUpdateDto("ConTent");
        PostViewDto viewDto = new PostViewDto(
                updateDto.content(), null, null, false, false, null
        );
        when(service.update(postId, updateDto)).thenReturn(viewDto);

        mockMvc.perform(put(STRING_API_V1 + "/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(updateDto.content()));
    }

    @Test
    @DisplayName("softDelete должен вернуть 204 No Content при успешном 'мягком' удалении")
    public void shouldReturnStatusNoContent_WhenSoftDeleteSuccessful() throws Exception {
        Long postId = 1L;
        doNothing().when(service).softDelete(postId);

        mockMvc.perform(delete(STRING_API_V1 + "/posts/{postId}", postId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }


    @Test
    @DisplayName("getById должен вернуть PostViewDto и статус 200 при успешном запросе")
    public void shouldReturnPost_WhenSuccessful() throws Exception {
        Long postId = 1L;
        PostViewDto dto = new PostViewDto(
                "Some", 1L, null, true, false, null
        );

        when(service.getById(postId)).thenReturn(dto);

        mockMvc.perform(get(STRING_API_V1 + "/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(dto.content()))
                .andExpect(jsonPath("$.authorId").value(dto.authorId()))
                .andExpect(jsonPath("$.published").value(dto.published()));
        verify(service, times(1)).getById(postId);
    }

    @Test
    @DisplayName("GET /users/{userId}/posts/drafts должен возвращать 200 и список черновиков")
    void shouldReturnUserDraftsWith200Status() throws Exception {
        Long userId = 1L;
        PostViewDto draft = getTestPostViewDto(false);
        when(service.getByUserDraftPostsSortedByCreation(userId))
                .thenReturn(List.of(draft));

        mockMvc.perform(get(STRING_API_V1 + "/users/{userId}/posts/drafts", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].published").value(false))
                .andExpect(jsonPath("$[0].content").exists());

        verify(service).getByUserDraftPostsSortedByCreation(userId);
    }

    @Test
    @DisplayName("GET /users/{userId}/posts/published должен возвращать 200 и список опубликованных постов")
    void shouldReturnUserPublishedPostsWith200Status() throws Exception {
        Long userId = 2L;
        PostViewDto publishedPost = getTestPostViewDto(true);
        when(service.getByUserPublishedPostsSortedByPublication(userId))
                .thenReturn(List.of(publishedPost));

        mockMvc.perform(get(STRING_API_V1 + "/users/{userId}/posts/published", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].published").value(true));

        verify(service).getByUserPublishedPostsSortedByPublication(userId);
    }

    @Test
    @DisplayName("GET /projects/{projectId}/posts/drafts должен возвращать 200 и список черновиков")
    void shouldReturnProjectDraftsWith200Status() throws Exception {
        Long projectId = 1L;
        PostViewDto draft = getTestPostViewDto(false);
        when(service.getByProjectDraftPostsSortedByCreation(projectId))
                .thenReturn(List.of(draft));

        mockMvc.perform(get(STRING_API_V1 + "/projects/{projectId}/posts/drafts", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].published").value(false));

        verify(service).getByProjectDraftPostsSortedByCreation(projectId);
    }

    @Test
    @DisplayName("GET /projects/{projectId}/posts/published должен возвращать 200 и список опубликованных постов")
    void shouldReturnProjectPublishedPostsWith200Status() throws Exception {
        Long projectId = 2L;
        PostViewDto publishedPost = getTestPostViewDto(true);
        when(service.getByProjectPublishedPostsSortedByPublication(projectId))
                .thenReturn(List.of(publishedPost));

        mockMvc.perform(get(STRING_API_V1 + "/projects/{projectId}/posts/published", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].published").value(true));

        verify(service).getByProjectPublishedPostsSortedByPublication(projectId);
    }

    private PostViewDto getTestPostViewDto(boolean flag) {
        return new PostViewDto(
                "content", 1L, null, flag, false, null
        );
    }

}