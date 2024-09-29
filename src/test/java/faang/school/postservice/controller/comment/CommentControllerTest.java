package faang.school.postservice.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.comment.CommentControllerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private CommentControllerValidator commentControllerValidator;

    @InjectMocks
    private CommentController commentController;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private CommentDto commentDto;
    private Long userId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        commentDto = new CommentDto();
        commentDto.setPostId(10L);
        commentDto.setAuthorId(20L);
        commentDto.setContent("Some content");
        objectMapper = new ObjectMapper();
        userId = 7L;
    }


    @Test
    void testCreateCommentSuccess() throws Exception {
        String commentDtoJson = objectMapper.writeValueAsString(commentDto);
        when(commentService.createComment(commentDto, userId)).thenReturn(commentDto);
        mockMvc.perform(post("/comment")
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(commentDto.getAuthorId()))
                .andExpect(jsonPath("$.postId").value(commentDto.getPostId()))
                .andExpect(jsonPath("$.content").value(commentDto.getContent()));

        verify(commentService, times(1)).createComment(commentDto, userId);
    }

    @Test
    void testGetComment() throws Exception {
        long commentId = 1L;
        List<CommentDto> commentDtos = List.of(new CommentDto(), new CommentDto());
        when(commentService.getComment(commentId)).thenReturn(commentDtos);

        mockMvc.perform(get("/comment/{commentId}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(commentDtos.size()));

        verify(commentService, times(1)).getComment(commentId);
    }

    @Test
    void testGetNoComments() throws Exception {
        long commentId = 1L;
        when(commentService.getComment(commentId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/comment/{commentId}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(0));

        verify(commentService, times(1)).getComment(commentId);
    }


    @Test
    void testDeleteComment() throws Exception {
        long commentId = 1L;
        doNothing().when(commentService).deleteComment(commentId);

        mockMvc.perform(delete("/comment/{commentId}", commentId))
                .andExpect(status().isOk());

        verify(commentService, times(1)).deleteComment(commentId);
    }

    @Test
    void testUpdateComment() throws Exception {
        Long commentId = 1L;
        String commentDtoJson = objectMapper.writeValueAsString(commentDto);
        when(commentService.updateComment(commentId, commentDto, userId)).thenReturn(commentDto);
        mockMvc.perform(put("/comment/{commentId}", commentId)
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(commentDto.getAuthorId()))
                .andExpect(jsonPath("$.postId").value(commentDto.getPostId()))
                .andExpect(jsonPath("$.content").value(commentDto.getContent()));

        verify(commentService, times(1)).updateComment(commentId, commentDto, userId);
    }
}