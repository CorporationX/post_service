package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {CommentController.class, UserContext.class})
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void positiveCreateComment() throws Exception {
        CommentDto inputDto = new CommentDto(1L, 1L, 1L, "Test", LocalDateTime.now());
        CommentDto outputDto = new CommentDto(1L, 1L, 1L, "Test", LocalDateTime.now());

        when(commentService.createComment(1L, 1L, inputDto)).thenReturn(outputDto);

        mockMvc.perform(post("/comments/create/{userId}/{postId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test"));
    }

    @Test
    void positiveEditComment() throws Exception {
        long commentId = 1L;
        String newContent = "Updated content";
        CommentDto inputDto = new CommentDto(
                commentId, 1L, 1L, "Old content", LocalDateTime.now());
        CommentDto outputDto = new CommentDto(
                commentId, 1L, 1L, newContent, LocalDateTime.now());

        when(commentService.editComment(any(CommentDto.class), eq(commentId), eq(newContent))).thenReturn(outputDto);

        mockMvc.perform(put("/comments/edit/{commentId}/{content}", 1, newContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.content").value(newContent));
    }

    @Test
    void positiveGetAllComments() throws Exception {
        long postId = 1L;
        List<CommentDto> mockComments = List.of(
                new CommentDto(1L, postId, 1L, "First comment", LocalDateTime.now()),
                new CommentDto(2L, postId, 1L, "Second comment", LocalDateTime.now())
        );

        when(commentService.getAllComments(postId)).thenReturn(mockComments);

        mockMvc.perform(get("/comments/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("First comment"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].content").value("Second comment"));
    }

    @Test
    void positiveDeleteComment() throws Exception {
        long commentId = 1L;
        doNothing().when(commentService).deleteComment(commentId);

        mockMvc.perform(delete("/comments/delete/{commentId}", commentId))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(commentId);
    }
}
