package faang.school.postservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentDto = createTestCommentDto();
    }


    @Test
    void createComment_Success() throws Exception {
        when(commentService.createComment(eq(5L), any(CommentDto.class))).thenReturn(commentDto);

        performPostRequest("/comments/post/5", commentDto)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }

    @Test
    void updateComment_Success() throws Exception {
        when(commentService.updateComment(eq(1L), any(CommentDto.class))).thenReturn(commentDto);

        performPutRequest("/comments/1", commentDto)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }

    @Test
    void getAllComments_Success() throws Exception {
        when(commentService.getAllComments(5L)).thenReturn(List.of(commentDto));

        performGetRequest("/comments/posts/5")
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(commentDto))));
    }

    @Test
    void deleteComment_Success() throws Exception {
        performDeleteRequest("/comments/1")
                .andExpect(status().isNoContent())
                .andExpect(content().string("Comment is deleted successfully"));

    }

    @Test
    void createComment_InvalidInput() throws Exception {
        CommentDto invalidCommentDto = new CommentDto();
        performPostRequest("/comment/post/5", invalidCommentDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_InvalidPost_BadRequest() throws Exception {
        performPostRequest("/comments/post/invalid", commentDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllComments_NonExistingPostId_ReturnNotFound() throws Exception {
        when(commentService.getAllComments(999L))
                .thenThrow(new RuntimeException("Post not found"));

        performGetRequest("/comments/posts/999")
                .andExpect(status().isNotFound())
                .andExpect(content().string("Post not found"));
    }

    @Test
    void updateComment_NonExistingCommentId_ReturnNotFound() throws Exception {
        when(commentService.updateComment(eq(999L), any(CommentDto.class)))
                .thenThrow(new RuntimeException("Comment not found"));
        performPutRequest("/comments/999", commentDto)
                .andExpect(status().isNotFound())
                .andExpect(content().string("Comment not found"));
    }

    @Test
    void creatComment_InternalServerError() throws Exception {
        when(commentService.createComment(eq(5L), any(CommentDto.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        performPostRequest("/comments/post/5", commentDto)
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Database connection error"));
    }
    @Test
    void deleteComment_NonExistentCommentId_ReturnNotFound() throws Exception{
        doThrow(new RuntimeException("Comment not found"))
                .when(commentService).deleteCommentById(999L);

                performDeleteRequest("/comments/999")
                        .andExpect(status().isNotFound())
                        .andExpect(content().string("Comment not found"));
    }
    private ResultActions performPostRequest(String url, CommentDto commentDto) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)));
    }

    private ResultActions performGetRequest(String url) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performDeleteRequest(String url) throws Exception {
        return mockMvc.perform(delete(url));
    }

    private ResultActions performPutRequest(String url, CommentDto commentDto) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)));
    }

    private CommentDto createTestCommentDto() {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setContent("Test Comment");
        dto.setAuthorId(100L);
        dto.setPostId(5L);
        dto.setCreatedAt(LocalDateTime.now());
        return dto;
    }
}