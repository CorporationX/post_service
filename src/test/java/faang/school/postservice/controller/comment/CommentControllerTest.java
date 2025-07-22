package faang.school.postservice.controller.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
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

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {
    private MockMvc mockMvc;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private CommentController commentController;
    @Spy
    private ObjectMapper objectMapper;

    private static final long POST_ID = 1;
    private static final long COMMENT_ID = 2;
    private static final long USER_ID = 2;
    private static final String CONTENT = "Test a content";
    private static final String NEW_CONTENT = "Test a new content";
    private static final int MAX_CONTENT_LENGTH = 4096;
    private static final String LONG_CONTENT = "A".repeat(MAX_CONTENT_LENGTH + 1);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    @DisplayName("Успешный вызов POST /v1/comments")
    void positive_shouldCreateComment() throws Exception {
        CommentDto commentDto = prepareCommentDto();
        CommentDto savedCommentDto = prepareSavedCommentDto(CONTENT);
        when(commentService.create(any(CommentDto.class))).thenReturn(savedCommentDto);

        mockMvc.perform(post("/v1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(jsonPath("$.id").value(COMMENT_ID),
                              jsonPath("$.authorId").value(USER_ID),
                              jsonPath("$.postId").value(POST_ID),
                              jsonPath("$.createAt").isNotEmpty(),
                              jsonPath("$.content").value(CONTENT));

        verify(commentService, times(1)).create(commentDto);
    }

    @Test
    @DisplayName("Успешный вызов PUT /v1/comments/{commentId}")
    void positive_shouldUpdateComment() throws Exception {
        CommentDto commentDto = prepareCommentDto();
        CommentDto savedCommentDto = prepareSavedCommentDto(NEW_CONTENT);
        when(commentService.update(anyLong(), any(CommentDto.class))).thenReturn(savedCommentDto);

        mockMvc.perform(put("/v1/comments/{commentId}", COMMENT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(jsonPath("$.id").value(COMMENT_ID),
                              jsonPath("$.authorId").value(USER_ID),
                              jsonPath("$.postId").value(POST_ID),
                              jsonPath("$.createAt").isNotEmpty(),
                              jsonPath("$.content").value(NEW_CONTENT));

        verify(commentService, times(1)).update(COMMENT_ID, commentDto);
    }

    @Test
    @DisplayName("Успешный вызов DELETE /v1/comments/{commentId}")
    void positive_shouldDeleteComment() throws Exception {
        mockMvc.perform(delete("/v1/comments/{commentId}", COMMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(commentService, times(1)).delete(COMMENT_ID);
    }

    @Test
    @DisplayName("Успешный вызов GET /v1/comments/posts/{postId}")
    void positive_shouldFindAllCommentByPostId() throws Exception {
        List<CommentDto> comments = List.of(prepareCommentDto());
        when(commentService.findAllByPostId(POST_ID)).thenReturn(comments);

        mockMvc.perform(get("/v1/comments/posts/{postId}", POST_ID))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().json(toJson(comments)));

        verify(commentService, times(1)).findAllByPostId(POST_ID);
    }

    @ParameterizedTest
    @MethodSource("provideNotValidComment")
    @DisplayName("Ошибка вызова POST /v1/comments - поля тела не валидны")
    void negative_whenDataDtoNotValid_returns400BadRequest(CommentDto commentDto) throws Exception {
        mockMvc.perform(post("/v1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(commentDto)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).create(any(CommentDto.class));
    }

    // -------------------

    private CommentDto prepareCommentDto() {
        return new CommentDto(null, CONTENT, USER_ID, 0, POST_ID, null, null);
    }

    private CommentDto prepareSavedCommentDto(String content) {
        return new CommentDto(COMMENT_ID, content, USER_ID, 0, POST_ID, LocalDateTime.now(), null);
    }

    private String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private static Stream<Arguments> provideNotValidComment() {
        return Stream.of(
                Arguments.of(new CommentDto(COMMENT_ID, null, USER_ID, 0, POST_ID, null, null)),
                Arguments.of(new CommentDto(COMMENT_ID, " ", USER_ID, 0, POST_ID, null, null)),
                Arguments.of(new CommentDto(COMMENT_ID, LONG_CONTENT, USER_ID, 0, POST_ID, null, null)),
                Arguments.of(new CommentDto(COMMENT_ID, CONTENT, null, 0, POST_ID, null, null)),
                Arguments.of(new CommentDto(COMMENT_ID, CONTENT, USER_ID, 0, null, null, null)));
    }
}