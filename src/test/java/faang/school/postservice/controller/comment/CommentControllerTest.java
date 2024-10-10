package faang.school.postservice.controller.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.dto.comment.CommentResponseDto;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private CommentController commentController;

    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .content("This is a comment")
                .authorId(1L)
                .postId(1L)
                .build();
    }

    @Test
    void create_shouldReturnCreatedComment() throws Exception {
        // given
        when(userContext.getUserId()).thenReturn(1L);
        when(commentService.create(anyLong(), any())).thenReturn(commentResponseDto);
        // when & then
        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "This is a comment",
                                  "authorId": 1,
                                  "postId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("This is a comment")))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.postId", is(1)));
    }

    @Test
    void update_shouldReturnUpdatedComment() throws Exception {
        // given
        when(commentService.update(any())).thenReturn(commentResponseDto);
        // when & then
        mockMvc.perform(put("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 1,
                                  "content": "Updated comment",
                                  "authorId": 1,
                                  "postId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("This is a comment")))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.postId", is(1)));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/comments/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void findAll_shouldReturnListOfComments() throws Exception {
        // given
        List<CommentResponseDto> comments = List.of(commentResponseDto);
        when(commentService.findAll(anyLong())).thenReturn(comments);
        // when & then
        mockMvc.perform(get("/api/v1/comments/postId/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("This is a comment")));
    }
}