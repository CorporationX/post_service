package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.comment.CommentController;
import faang.school.postservice.dto.comment.CommentForCreationDto;
import faang.school.postservice.dto.comment.CommentForUpdateDto;
import faang.school.postservice.dto.comment.CommentOutputDto;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService service;

    @Mock
    private UserContext userContext;

    @Spy
    private CommentMapperImpl commentMapper;

    @InjectMocks
    private CommentController controller;

    private long postId = 1L;
    private long userId = 3L;
    private long commentId1 = 22L;
    private Comment comment1;
    private Comment comment2;
    private CommentOutputDto outputDto;
    private CommentForCreationDto creationDto;
    private CommentForUpdateDto updateDto;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        comment1 = Comment.builder()
                .post(Post.builder().id(postId).build())
                .content("Comment 1")
                .id(commentId1).build();
        comment2 = Comment.builder()
                .content("Comment 2")
                .post(Post.builder().id(postId).build())
                .build();
        when(userContext.getUserId()).thenReturn(userId);
    }

    @Test
    void testCreate() throws Exception {
        CommentForCreationDto commentDto = new CommentForCreationDto(postId, "Comment 1");

        outputDto = commentMapper.toDto(comment1);
        when(service.create(any(CommentForCreationDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/api/v1/comments")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Comment 1")));

        verify(service).create(any(CommentForCreationDto.class));
    }

    @Test
    void testUpdate() throws Exception {
        CommentForUpdateDto commentDto = new CommentForUpdateDto(commentId1, "Updated comment");
        comment1.setContent("Updated comment");
        outputDto = commentMapper.toDto(comment1);

        when(service.update(any(CommentForUpdateDto.class))).thenReturn(outputDto);

        mockMvc.perform(patch("/api/v1/comments")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Updated comment")));

        verify(service).update(any(CommentForUpdateDto.class));
    }

    @Test
    void testFindByPostId() throws Exception {
        List<CommentOutputDto> expectedComments =
                commentMapper.toListDto(Arrays.asList(comment1, comment2));

        when(service.findByPostId(postId)).thenReturn(expectedComments);

        mockMvc.perform(get("/api/v1/comments/post/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content", is("Comment 1")))
                .andExpect(jsonPath("$[1].content", is("Comment 2")));
    }

    @Test
    void testFindById() throws Exception {
        outputDto = commentMapper.toDto(comment1);
        when(service.findById(commentId1)).thenReturn(outputDto);

        mockMvc.perform(get("/api/v1/comments/{commentId1}", commentId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Comment 1")));
    }

    @Test
    void testDeleteById() throws Exception {
        mockMvc.perform(delete("/api/v1/comments/{commentId1}", commentId1))
                .andExpect(status().isNoContent());

        verify(service).deleteById(commentId1);
    }
}