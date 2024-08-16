package faang.school.postservice.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;



import java.util.List;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    @InjectMocks
    private CommentController commentController;
    @Mock
    private CommentService commentService;
    private MockMvc mockMvc;

    List<CommentDto> comments;
    CommentDto commentOne;
    CommentDto commentTwo;

    @BeforeEach
    void setUp() {
        commentOne = CommentDto.builder().id(1l).content("First content").postId(2l).authorId(1l).build();
        commentTwo = CommentDto.builder().id(2L).content("Second content").build();
        comments = List.of(commentOne, commentTwo);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    void findAllByPostIdTest() throws Exception {
        long postId = 1L;
        when(commentService.findAllByPostId(postId)).thenReturn(comments);
        mockMvc.perform(get("/api/v1/comments/posts/{id}", postId))
                .andExpect(status().isOk());
        verify(commentService, times(1)).findAllByPostId(postId);
    }

    @Test
    void deleteCommentTest() throws Exception {
        long commentId = 1L;
        doNothing().when(commentService).deleteComment(commentId);
        mockMvc.perform(delete("/api/v1/comments/{id}", commentId));
        verify(commentService, times(1)).deleteComment(commentId);
    }

    @Test
    void createCommentTest()  {
        when(commentService.createComment(commentOne)).thenReturn(commentOne);
        commentController.createComment(commentOne);
        verify(commentService, times(1)).createComment(commentOne);
    }

    @Test
    void updateCommentTest()  {
        when(commentService.updateComment(commentOne)).thenReturn(commentOne);
        commentController.updateComment(commentOne);
        verify(commentService, times(1)).updateComment(commentOne);
    }
}
