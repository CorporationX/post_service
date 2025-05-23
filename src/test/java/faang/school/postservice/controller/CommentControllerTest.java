package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;

    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        commentDto = new CommentDto(
                1L,
                "Test content",
                2L,
                3L,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should create a comment and return the created comment DTO")
    void createCommentTest_shouldReturnCreatedComment() throws Exception {
        when(commentService.createComment(any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "content": "Test content",
                                    "authorId": 2,
                                    "postId": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.content").value(commentDto.getContent()));

        verify(commentService, times(1)).createComment(any(CommentDto.class));
    }

    @Test
    @DisplayName("Should update a comment content and return the updated comment DTO")
    void updateCommentContentTest_shouldReturnUpdatedComment() throws Exception {
        when(commentService.updateCommentContent(eq(1L), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(put("/api/v1/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "content": "Updated content",
                                    "authorId": 2,
                                    "postId": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()));

        verify(commentService, times(1)).updateCommentContent(eq(1L), any(CommentDto.class));
    }

    @Test
    @DisplayName("Should return a list of comments for the given filter")
    void getAllCommentsTest_shouldReturnListOfComments() throws Exception {
        when(commentService.getAllComments(any(CommentDto.class))).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/api/v1/comment/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "postId": 3,
                                    "content": "Any",
                                    "authorId": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(commentDto.getId()));

        verify(commentService, times(1)).getAllComments(any(CommentDto.class));
    }

    @Test
    @DisplayName("Should delete a comment by ID")
    void deleteCommentTest_shouldCallServiceDelete() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/api/v1/comment/1"))
                .andExpect(status().isOk());

        verify(commentService, times(1)).deleteComment(1L);
    }
}
