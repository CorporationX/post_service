package faang.school.postservice.controller;

import faang.school.postservice.controller.comment.CommentController;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService service;

    @Mock
    private CommentValidator validator;

    @InjectMocks
    private CommentController controller;

    private long postId = 1L;
    private long commentId1 = 22L;
    private CommentDto comment1;
    private CommentDto comment2;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        comment1 = CommentDto.builder()
                .content("Comment 1")
                .id(commentId1).build();
        comment2 = CommentDto.builder()
                .content("Comment 2").build();
    }

    @Test
    void create() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(44L)
                .content("New comment")
                .build();

        when(service.create(any(CommentDto.class))).thenReturn(commentDto);
        doNothing().when(validator).validate(any(CommentDto.class));

        mockMvc.perform(post("/api/comments")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("New comment")));

        verify(service).create(any(CommentDto.class));
    }

    @Test
    void update() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(44L)
                .content("Updated comment")
                .build();

        when(service.update(any(CommentDto.class))).thenReturn(commentDto);
        doNothing().when(validator).validate(any(CommentDto.class));

        mockMvc.perform(put("/api/comments")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Updated comment")));

        verify(service).update(any(CommentDto.class));
    }

    @Test
    void findByPostId() throws Exception {
        List<CommentDto> expectedComments = Arrays.asList(comment1, comment2);

        when(service.findByPostId(postId)).thenReturn(expectedComments);

        mockMvc.perform(get("/api/comments/post/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content", is("Comment 1")))
                .andExpect(jsonPath("$[1].content", is("Comment 2")));
    }

    @Test
    void findById() throws Exception {
        CommentDto expectedCommentDto = comment1;

        when(service.findById(commentId1)).thenReturn(expectedCommentDto);

        mockMvc.perform(get("/api/comments/{commentId1}", commentId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Comment 1")));
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete("/api/comments/{commentId1}", commentId1))
                .andExpect(status().isNoContent());

        verify(service).deleteById(commentId1);
    }
}