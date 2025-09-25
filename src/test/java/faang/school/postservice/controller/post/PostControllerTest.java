package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.util.JsonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {PostController.class, JsonMapper.class})
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper jsonMapper;
    @MockBean
    private PostService service;

    private static final long POST_ID = 1;
    private static final long AUTHOR_ID = 10;
    private static final String CONTENT = "post text";

    @Test
    @DisplayName("200 ОК - POST /v1/posts")
    void positive_shouldCallCreate() throws Exception {
        PostDto dto = createDto(CONTENT);
        PostDto expected = prepareSavedPostDto(CONTENT);
        when(service.create(any(PostDto.class))).thenReturn(expected);

        mockMvc.perform(post("/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.toJson(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(jsonPath("$.id").value(POST_ID),
                        jsonPath("$.authorId").value(AUTHOR_ID),
                        jsonPath("$.createdAt").isNotEmpty(),
                        jsonPath("$.content").value(CONTENT));

        verify(service, times(1)).create(dto);
    }

    @Test
    @DisplayName("200 ОК - POST /v1/posts/{id}/publish")
    void positive_shouldCallPublish() throws Exception {
        mockMvc.perform(patch("/v1/posts/{id}/publish", POST_ID))
                .andExpect(status().isOk());

        verify(service, times(1)).publish(POST_ID);
    }

    private static Stream<Arguments> provideNotValidPost() {
        return Stream.of(
                Arguments.of(new PostDto(null, AUTHOR_ID, null, null, 0, 0, null, null)),
                Arguments.of(new PostDto(null, AUTHOR_ID, null, "", 0, 0, null, null)));
    }

    @ParameterizedTest
    @MethodSource("provideNotValidPost")
    @DisplayName("Ошибка вызова POST /v1/posts - поля тела не валидны")
    void negative_whenDataDtoNotValid_returns400BadRequest(PostDto dto) throws Exception {
        mockMvc.perform(post("/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.toJson(dto)))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any(PostDto.class));
    }

    // ----------------------------

    private PostDto createDto(String content) {
        return PostDto.builder()
                .content(content)
                .build();
    }

    private PostDto prepareSavedPostDto(String content) {
        return PostDto.builder()
                .id(POST_ID)
                .content(content)
                .authorId(AUTHOR_ID)
                .createdAt(LocalDateTime.now())
                .build();
    }
}