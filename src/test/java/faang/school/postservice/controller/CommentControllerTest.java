package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserContext userContext;

    private CommentCreateDto commentCreateDto;
    private CommentViewDto commentViewDto;
    private final static long VALID_ID = 1L;
    private final static long INVALID_ID = 999L;

    @BeforeEach
    void setUp() {
        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setContent("content");
        commentCreateDto.setAuthorId(1L);
        commentCreateDto.setPostId(1L);

        commentViewDto = new CommentViewDto();
        commentViewDto.setId(1L);
        commentViewDto.setContent("content");
        commentViewDto.setAuthorId(1L);
        commentViewDto.setPostId(1L);
    }

    @Nested
    @DisplayName("POST /posts/{postId}/comments - Создание комментария")
    class CreateCommentTests {

        @Test
        @DisplayName("Должен успешно создать комментарий и вернуть CommentViewDto со статусом 200 OK")
        void givenValidRequest_WhenCreateComment_ThenReturnOkWithCommentData() throws Exception {
            Mockito.when(commentService.createComment(VALID_ID, commentCreateDto))
                    .thenReturn(commentViewDto);

            mockMvc.perform(post("/posts/{postId}/comments", VALID_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем postId")
        void givenNonExistentPostId_WhenCreateComment_ThenReturnNotFound() throws Exception {
            Mockito.when(commentService.createComment(VALID_ID, commentCreateDto))
                    .thenThrow(new EntityNotFoundException("Post with id 1 not found"));

            mockMvc.perform(post("/posts/{postId}/comments", VALID_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Должен вернуть 400 Bad Request при null-содержимом")
        void givenNullContent_WhenCreateComment_ThenReturnBadRequest() throws Exception {
            commentCreateDto.setContent(null);

            mockMvc.perform(post("/posts/{postId}/comments", VALID_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем authorId")
        void givenNonExistentAuthorId_WhenCreateComment_ThenReturnNotFound() throws Exception {
            Mockito.when(commentService.createComment(VALID_ID, commentCreateDto))
                    .thenThrow(new EntityNotFoundException("Author with id 1 not found"));

            mockMvc.perform(post("/posts/{postId}/comments", VALID_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /posts/{postId}/comments/{commentId} - Обновление комментария")
    class UpdateCommentTests {

        @Test
        @DisplayName("Должен успешно обновить комментарий и вернуть CommentViewDto со статусом 200 OK")
        void givenValidRequest_WhenUpdateComment_ThenReturnOkWithUpdatedComment() throws Exception {
            Mockito.when(commentService.updateComment(VALID_ID, VALID_ID, commentCreateDto))
                    .thenReturn(commentViewDto);

            mockMvc.perform(put("/posts/{postId}/comments/{commentId}", VALID_ID, VALID_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(VALID_ID))
                    .andExpect(jsonPath("$.content").value("content"));
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем commentId")
        void givenNonExistentCommentId_WhenUpdateComment_ThenReturnNotFound() throws Exception {
            Mockito.when(commentService.updateComment(VALID_ID, INVALID_ID, commentCreateDto))
                    .thenThrow(new EntityNotFoundException("Comment not found"));

            mockMvc.perform(put("/posts/{postId}/comments/{commentId}", VALID_ID, INVALID_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Должен вернуть 400 Bad Request при null-содержимом")
        void givenNullContent_WhenUpdateComment_ThenReturnBadRequest() throws Exception {
            commentCreateDto.setContent(null);

            mockMvc.perform(put("/posts/{postId}/comments/{commentId}", VALID_ID, VALID_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentCreateDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /posts/{postId}/comments - Получение комментариев поста")
    class GetCommentsTests {

        @Test
        @DisplayName("Должен вернуть список комментариев со статусом 200 OK")
        void givenExistingPostId_WhenGetComments_ThenReturnOkWithCommentsList() throws Exception {
            List<CommentViewDto> comments = List.of(commentViewDto);
            Mockito.when(commentService.getCommentsByPostId(VALID_ID))
                    .thenReturn(comments);

            mockMvc.perform(get("/posts/{postId}/comments", VALID_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(VALID_ID))
                    .andExpect(jsonPath("$[0].content").value("content"))
                    .andExpect(jsonPath("$[0].postId").value(VALID_ID));
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем postId")
        void givenNonExistentPostId_WhenGetComments_ThenReturnNotFound() throws Exception {
            Mockito.when(commentService.getCommentsByPostId(VALID_ID))
                    .thenThrow(new EntityNotFoundException("Post not found"));

            mockMvc.perform(get("/posts/{postId}/comments", VALID_ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /posts/{postId}/comments/{commentId} - Удаление комментария")
    class DeleteCommentTests {

        @Test
        @DisplayName("Должен успешно удалить комментарий и вернуть 204 No Content")
        void givenValidCommentId_WhenDeleteComment_ThenReturnNoContent() throws Exception {
            Mockito.doNothing().when(commentService).deleteComment(VALID_ID, VALID_ID);

            mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", VALID_ID, VALID_ID))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем commentId")
        void givenNonExistentCommentId_WhenDeleteComment_ThenReturnNotFound() throws Exception {
            Mockito.doThrow(new EntityNotFoundException("Comment not found"))
                    .when(commentService).deleteComment(VALID_ID, INVALID_ID);

            mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", VALID_ID, INVALID_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found при несуществующем postId")
        void givenNonExistentPostId_WhenDeleteComment_ThenReturnNotFound() throws Exception {
            Mockito.doThrow(new EntityNotFoundException("Post not found"))
                    .when(commentService).deleteComment(INVALID_ID, VALID_ID);

            mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", INVALID_ID, VALID_ID))
                    .andExpect(status().isNotFound());
        }
    }
}