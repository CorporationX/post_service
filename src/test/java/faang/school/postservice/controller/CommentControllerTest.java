package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.*;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long postId = 1L;
    private final Long commentId = 10L;
    private final Long userId = 42L;

    private final CommentDto mockCommentDto = CommentDto.builder()
            .id(commentId)
            .content("Test content")
            .authorId(userId)
            .postId(postId)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    @Test
    @DisplayName("GET /posts/{postId}/comments — success")
    void getComments_shouldReturnList() throws Exception {
        when(commentService.getCommentsByPostId(postId))
                .thenReturn(List.of(mockCommentDto));

        mockMvc.perform(get("/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(commentId));
    }

    @Test
    @DisplayName("POST /posts/{postId}/comments — success")
    void createComment_shouldReturnCreated() throws Exception {
        CommentCreateDto createDto = CommentCreateDto.builder()
                .content("New comment")
                .build();

        when(userContext.getUserId()).thenReturn(userId);
        when(commentService.createComment(Mockito.eq(postId),
                Mockito.any(CommentCreateDto.class), Mockito.eq(userId)))
                .thenReturn(mockCommentDto);

        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentId));
    }

    @Test
    @DisplayName("PUT /posts/{postId}/comments/{commentId} — success")
    void updateComment_shouldReturnUpdated() throws Exception {
        CommentUpdateDto updateDto = CommentUpdateDto.builder()
                .content("Updated comment")
                .build();

        when(userContext.getUserId()).thenReturn(userId);
        when(commentService.updateComment(commentId, updateDto, userId))
                .thenReturn(mockCommentDto);

        mockMvc.perform(put("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId));
    }

    @Test
    @DisplayName("DELETE /posts/{postId}/comments/{commentId} — success")
    void deleteComment_shouldReturnNoContent() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isNoContent());
    }
}
