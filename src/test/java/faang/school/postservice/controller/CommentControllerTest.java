package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CreateCommentDto createDto;
    private CommentResponseDto responseDto;
    private UpdateCommentDto updateDto;
    private Comment comment;
    private Long postId = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper = new ObjectMapper();
        createDto = mockCreateCommentDto();
        updateDto = mockUpdateCommentDto();
        responseDto = mockCommentDto();
        comment = mockComment();
    }

    @Test
    @DisplayName("Create comment success")
    void testCreateCommentSuccess() throws Exception {
        when(commentService.createComment(postId, createDto)).thenReturn(responseDto);


        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.authorId").value(responseDto.getAuthorId()));

        verify(commentService, times(1)).createComment(postId, createDto);
    }

    @Test
    @DisplayName("Create comment fail")
    void testCreateCommentFail() {
        when(commentService.createComment(postId, createDto)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> commentController.create(postId, createDto));
    }

    @Test
    @DisplayName("Update comment success")
    void testUpdateCommentSuccess() throws Exception {
        when(commentService.updateComment(postId, updateDto)).thenReturn(responseDto);


        mockMvc.perform(put("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.content").value(responseDto.getContent()))
                .andExpect(jsonPath("$.authorId").value(responseDto.getAuthorId()));

        verify(commentService, times(1)).updateComment(postId, updateDto);
    }

    @Test
    @DisplayName("Update comment fail")
    void testUpdateCommentFail() {
        when(commentService.updateComment(postId, updateDto)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> commentController.update(postId, updateDto));
    }

    @Test
    @DisplayName("Get all comments success")
    void testGetAllCommentsSuccess() throws Exception {
        List<CommentResponseDto> comments = List.of(responseDto, responseDto, responseDto);
        when(commentService.getAllComments(postId)).thenReturn(comments);

        mockMvc.perform(get("/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(comments.get(0).getId()))
                .andExpect(jsonPath("$[2].authorId").value(comments.get(2).getAuthorId()))
                .andExpect(jsonPath("$", hasSize(3)));

        verify(commentService, times(1)).getAllComments(postId);
    }

    @Test
    @DisplayName("Get all comments fail")
    void testGetAllComments() {
        when(commentService.getAllComments(postId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> commentService.getAllComments(postId));
    }

    @Test
    @DisplayName("Delete comment by id success")
    void testDeleteCommentByIdSuccess() throws Exception {
        doNothing().when(commentService).deleteComment(postId, comment.getId());

        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, comment.getId()))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(postId, comment.getId());
    }

    @Test
    @DisplayName("Delete comment by id fail: id")
    void testDeleteCommentByIdFailInvalidId() {
        doThrow(EntityNotFoundException.class).when(commentService).deleteComment(postId, comment.getId());

        assertThrows(EntityNotFoundException.class, () -> commentController.deleteComment(postId, comment.getId()));

        verify(commentService, times(1)).deleteComment(postId, comment.getId());
    }

    private CreateCommentDto mockCreateCommentDto() {
        return CreateCommentDto.builder()
                .authorId(1L)
                .content("Test content")
                .build();
    }

    private UpdateCommentDto mockUpdateCommentDto() {
        return UpdateCommentDto.builder()
                .id(3L)
                .authorId(1L)
                .content("Test update content")
                .build();
    }

    private CommentResponseDto mockCommentDto() {
        return CommentResponseDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .likeIds(null)
                .postId(1L)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    private Comment mockComment() {
        return Comment.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .likes(null)
                .post(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }
}