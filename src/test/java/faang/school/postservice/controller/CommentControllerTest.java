package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                3L
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
        when(commentService.updateCommentContent(any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(put("/api/v1/comment/")
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

        verify(commentService, times(1)).updateCommentContent(any(CommentDto.class));
    }

    @Test
    @DisplayName("Should return a list of comments filtered by postId and authorId")
    void getAllCommentsTest_shouldReturnListOfComments() throws Exception {
        when(commentService.getAllComments(eq(3L))).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/api/v1/comment/all")
                        .param("postId", "3")
                        .param("authorId", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(commentDto.getId()));

        verify(commentService, times(1)).getAllComments(eq(3L));
    }
}
