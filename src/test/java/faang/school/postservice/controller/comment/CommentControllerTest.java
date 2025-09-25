package faang.school.postservice.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static faang.school.postservice.controller.comment.CommentControllerTestData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @MockBean
    UserContext userContext;

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ValidRequest_ReturnsCreated() throws Exception {
        CommentCreateDto createDto = new CommentCreateDto(
                "Test content", AUTHOR_ID, POST_ID, "large.jpg", "small.jpg"
        );
        CommentViewDto viewDto = new CommentViewDto(
                COMMENT_ID, "Test content", AUTHOR_ID, POST_ID, "large.jpg", "small.jpg"
        );

        when(commentService.create(any(CommentCreateDto.class), eq(POST_ID))).thenReturn(viewDto);

        mockMvc.perform(post("/posts/{postId}/comments", POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(COMMENT_ID))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.authorId").value(AUTHOR_ID));

        verify(commentService).create(any(CommentCreateDto.class), eq(POST_ID));
    }

    @Test
    void create_InvalidRequest_ReturnsBadRequest() throws Exception {
        CommentCreateDto invalidDto = new CommentCreateDto(
                "", null, null, null, null
        );

        mockMvc.perform(post("/posts/{postId}/comments", POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Test
    void update_ValidRequest_ReturnsOk() throws Exception {
        CommentUpdateDto updateDto = new CommentUpdateDto(
                "Updated content", AUTHOR_ID, POST_ID, "large-updated.jpg", "small-updated.jpg"
        );
        CommentViewDto viewDto = new CommentViewDto(
                COMMENT_ID, "Updated content", AUTHOR_ID, POST_ID, "large-updated.jpg", "small-updated.jpg"
        );

        when(commentService.update(eq(POST_ID), eq(COMMENT_ID), any(CommentUpdateDto.class)))
                .thenReturn(viewDto);

        mockMvc.perform(put("/posts/{postId}/comments/{commentId}", POST_ID, COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COMMENT_ID))
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.largeImageFileKey").value("large-updated.jpg"));

        verify(commentService).update(eq(POST_ID), eq(COMMENT_ID), any(CommentUpdateDto.class));
    }

    @Test
    void getAllCommentsByPostId_ValidRequest_ReturnsComments() throws Exception {
        CommentViewDto comment1 = new CommentViewDto(
                1L, "Comment 1", 1L, POST_ID, "img1.jpg", "thumb1.jpg"
        );
        CommentViewDto comment2 = new CommentViewDto(
                2L, "Comment 2", 2L, POST_ID, "img2.jpg", "thumb2.jpg"
        );
        List<CommentViewDto> comments = List.of(comment1, comment2);

        when(commentService.getAllCommentByPostId(POST_ID)).thenReturn(comments);

        mockMvc.perform(get("/posts/{postId}/comments", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Comment 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].content").value("Comment 2"));

        verify(commentService).getAllCommentByPostId(POST_ID);
    }

    @Test
    void getAllCommentsByPostId_NoComments_ReturnsEmptyList() throws Exception {
        when(commentService.getAllCommentByPostId(POST_ID)).thenReturn(List.of());

        mockMvc.perform(get("/posts/{postId}/comments", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(commentService).getAllCommentByPostId(POST_ID);
    }

    @Test
    void delete_ValidRequest_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", POST_ID, COMMENT_ID))
                .andExpect(status().isNoContent());

        verify(commentService).delete(POST_ID, COMMENT_ID);
    }
}