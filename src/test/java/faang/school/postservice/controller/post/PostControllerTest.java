package faang.school.postservice.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    private PostService service;

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

}