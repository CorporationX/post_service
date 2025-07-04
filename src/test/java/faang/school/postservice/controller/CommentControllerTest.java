package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserContext userContext;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateComment() throws Exception {
        CommentCreateDto dto = CommentCreateDto.builder()
                .postId(1L)
                .content("Great post!")
                .build();

        CommentDto response = CommentDto.builder()
                .id(1L)
                .postId(1L)
                .content("Great post!")
                .build();

        Mockito.when(commentService.createComment(any(), eq(100L)))
                .thenReturn(response);

        mockMvc.perform(post("/comments")
                        .param("userId", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Great post!"));
    }

    @Test
    void testUpdateComment() throws Exception {
        CommentUpdateDto dto = CommentUpdateDto.builder()
                .commentId(1L)
                .content("Updated comment")
                .build();

        CommentDto response = CommentDto.builder()
                .id(1L)
                .postId(1L)
                .content("Updated comment")
                .build();

        Mockito.when(commentService.updateComment(any(),
                eq(101L))).thenReturn(response);

        mockMvc.perform(put("/comments")
                        .param("userId", "101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content")
                        .value("Updated comment"));
    }

    @Test
    void testDeleteComment() throws Exception {
        mockMvc.perform(delete("/comments/1")
                        .param("userId", "200"))
                .andExpect(status().isNoContent());

        Mockito.verify(commentService).deleteComment(1L, 200L);
    }

    @Test
    void testGetCommentsByPostId() throws Exception {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .postId(10L)
                .content("Nice!")
                .build();

        Mockito.when(commentService.getCommentsByPostId(10L))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/comments/post/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("Nice!"));
    }
}
