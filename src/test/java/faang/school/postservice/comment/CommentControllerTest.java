package faang.school.postservice.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.controller.comment.CommentController;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private CommentController controller;
    @Mock
    private CommentService service;
    private long postId;
    private long commentId;
    private CommentDto commentDto;
    private String json;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        postId = 1L;
        commentId = 2L;
        commentDto = CommentDto.builder()
                .id(commentId)
                .content("Лайк")
                .authorId(1L)
                .postId(postId)
                .build();
        json = new ObjectMapper().writeValueAsString(commentDto);
    }
    @Test
    public void testGetComments() throws Exception {
        // Arrange
        List<CommentDto> expectedComments = Arrays.asList(
                CommentDto.builder().content("Лайк").build(),
                CommentDto.builder().content("Дизлайк").build()
        );
        long postId = 1L;
        when(service.getComments(any())).thenReturn(expectedComments);

        // Act and Assert
        mockMvc.perform(post("/api/comments?postId=" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0].content", is("Лайк")))
                .andExpect(jsonPath("[1].content", is("Дизлайк")));
    }

    @Test
    public void testAddComment() throws Exception {
        // Arrange
        CommentDto expectedCommentDto = CommentDto.builder()
                .id(commentId)
                .content("Лайк")
                .authorId(1L)
                .postId(postId)
                .build();
        when(service.addComment(any(), any())).thenReturn(expectedCommentDto);

        // Act and Assert
        mockMvc.perform(post("/api/comment?postId=" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", is("Лайк")))
                .andExpect(jsonPath("postId", is((int) postId)))
                .andExpect(jsonPath("id", is((int) commentId)));
    }

    @Test
    public void testUpdateComment() throws Exception {
        // Arrange
        String newContent = "Дизлайк";
        CommentDto expectedCommentDto = CommentDto.builder()
                .id(commentId)
                .postId(postId)
                .content(newContent)
                .build();
        when(service.updateComment(any(), any())).thenReturn(expectedCommentDto);

        // Act and Assert
        mockMvc.perform(put("/api/comment?postId=" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", is(newContent)))
                .andExpect(jsonPath("postId", is((int) postId)))
                .andExpect(jsonPath("id", is((int) commentId)));
    }

    @Test
    public void testDeleteComment() throws Exception {
        // Arrange
        CommentDto expectedCommentDto = CommentDto.builder()
                .id(commentId)
                .postId(postId)
                .content(commentDto.getContent())
                .build();
        when(service.deleteComment(any(), any())).thenReturn(expectedCommentDto);

        // Act and Assert
        mockMvc.perform(delete("/api/comment?postId=" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is((int) commentId)))
                .andExpect(jsonPath("content", is(commentDto.getContent())))
                .andExpect(jsonPath("postId", is((int) postId)));
    }
}
