package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.controller.comment.CommentController;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentPostRequestDto;
import faang.school.postservice.dto.comment.CommentPostResponseDto;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.any;
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
public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Spy
    private CommentMapperImpl commentMapper;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Long POST_ID = 1L;
    private static final Long COMMENT_ID = 2L;
    private static final Long AUTHOR_ID = 3L;
    private static final String CONTENT = "Test Content";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }


    @Test
    void testAddComment() throws Exception {
        CommentPostRequestDto requestDto = CommentPostRequestDto.builder().content(CONTENT).authorId(AUTHOR_ID).build();
        CommentDto commentDto = CommentDto.builder().content(CONTENT).authorId(AUTHOR_ID).postId(POST_ID).build();
        CommentPostResponseDto responseDto = CommentPostResponseDto.builder().id(COMMENT_ID).content(CONTENT)
                .authorId(AUTHOR_ID).postId(POST_ID).build();

        when(commentService.addComment(any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/comments/{postId}", POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(CONTENT))
                .andExpect(jsonPath("$.authorId").value(AUTHOR_ID))
                .andExpect(jsonPath("$.postId").value(POST_ID));

        verify(commentService, times(1)).addComment(any(CommentDto.class));
    }

    @Test
    void testUpdateComment() throws Exception {
        CommentPostRequestDto requestDto = CommentPostRequestDto.builder().content(CONTENT).authorId(AUTHOR_ID).build();
        CommentDto commentDto = CommentDto.builder().id(COMMENT_ID).content(CONTENT).authorId(AUTHOR_ID)
                .postId(POST_ID).build();
        CommentPostResponseDto responseDto = CommentPostResponseDto.builder().id(COMMENT_ID).content(CONTENT)
                .authorId(AUTHOR_ID).postId(POST_ID).build();

        when(commentService.updateComment(any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(put("/comments/{postId}/{commentId}", POST_ID, COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(CONTENT))
                .andExpect(jsonPath("$.authorId").value(AUTHOR_ID))
                .andExpect(jsonPath("$.postId").value(POST_ID));

        verify(commentService, times(1)).updateComment(any(CommentDto.class));
    }

    @Test
    void testGetAllComments() throws Exception {
        CommentDto commentDto = CommentDto.builder().id(COMMENT_ID).content(CONTENT).authorId(AUTHOR_ID)
                .postId(POST_ID).build();
        CommentPostResponseDto responseDto = CommentPostResponseDto.builder().id(COMMENT_ID).content(CONTENT)
                .authorId(AUTHOR_ID).postId(POST_ID).build();
        List<CommentDto> commentDtos = List.of(commentDto);
        List<CommentPostResponseDto> responseDtos = List.of(responseDto);

        when(commentService.getAllComments(POST_ID)).thenReturn(commentDtos);

        mockMvc.perform(get("/comments/post/" + POST_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(CONTENT))
                .andExpect(jsonPath("$[0].authorId").value(AUTHOR_ID))
                .andExpect(jsonPath("$[0].postId").value(POST_ID));

        verify(commentService, times(1)).getAllComments(POST_ID);
    }

    @Test
    void testDeleteComment() throws Exception {
        mockMvc.perform(delete("/comments/{postId}/{commentId}", POST_ID, COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(COMMENT_ID);
    }
}