package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@ExtendWith(SpringExtension.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserContext userContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /comments/{postId} - success")
    void getCommentsByPostId_success() throws Exception {
        Long postId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .postId(postId)
                .authorId(100L)
                .content("Test comment")
                .build();

        Mockito.when(commentService.getCommentsByPostId(postId))
                .thenReturn(Collections.singletonList(commentDto));

        mockMvc.perform(get("/api/v1/comments/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value(postId))
                .andExpect(jsonPath("$[0].content").value("Test comment"));
    }

    @Test
    @DisplayName("POST /comments - success")
    void createComment_success() throws Exception {
        CommentDto requestDto = CommentDto.builder()
                .postId(1L)
                .authorId(100L)
                .content("New comment")
                .build();

        Mockito.when(commentService.createComment(any(CommentDto.class)))
                .thenReturn(requestDto);

        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("New comment"));
    }

    @Test
    @DisplayName("PUT /comments/{commentId} - success")
    void updateComment_success() throws Exception {
        Long commentId = 5L;
        Long userId = 100L;

        CommentDto requestDto = CommentDto.builder()
                .postId(1L)
                .authorId(userId)
                .content("Updated comment")
                .build();

        Mockito.when(commentService.updateComment(eq(commentId), any(CommentDto.class), eq(userId)))
                .thenReturn(requestDto);

        mockMvc.perform(put("/api/v1/comments/{commentId}", commentId)
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated comment"));
    }

    @Test
    @DisplayName("DELETE /comments/{commentId} - success")
    void deleteComment_success() throws Exception {
        Long commentId = 10L;
        Long userId = 100L;

        mockMvc.perform(delete("/api/v1/comments/{commentId}", commentId)
                        .header("x-user-id", userId))
                .andExpect(status().isNoContent());

        Mockito.verify(commentService).deleteComment(commentId, userId);
    }
}
