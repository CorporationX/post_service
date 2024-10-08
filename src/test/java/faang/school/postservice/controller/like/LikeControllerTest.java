package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.impl.like.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeServiceImpl likeService;

    private LikeDto likeDto;
    private long commentAndPostId;

    @BeforeEach
    void setUp() {
        likeDto = new LikeDto();
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
        commentAndPostId = 1L;
    }

    @Test
    void createLikeComment() throws Exception {
        likeDto.setId(1L);
        likeDto.setPostId(null);
        likeDto.setCommentId(1L);
        likeDto.setUserId(1L);


        Mockito.when(likeService.createLikeComment(Mockito.anyLong())).thenReturn(likeDto);

        mockMvc.perform(post("/api/v1/like/commentId/{commentId}", commentAndPostId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.commentId").value(1L));
    }

    @Test
    void deleteLikeComment() throws Exception {

        Mockito.doNothing().when(likeService).deleteLikeComment(commentAndPostId);

        mockMvc.perform(delete("/api/v1/like/commentId/{commentId}", commentAndPostId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Comment with id " + commentAndPostId + " was deleted successfully."));
    }

    @Test
    void createLikePost() throws Exception {
        likeDto.setId(1L);
        likeDto.setPostId(1L);
        likeDto.setCommentId(null);
        likeDto.setUserId(1L);
        Mockito.when(likeService.createLikePost(Mockito.anyLong())).thenReturn(likeDto);

        mockMvc.perform(post("/api/v1/like/postId/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.postId").value(1L));
    }

    @Test
    void deleteLikePost() throws Exception {
        Mockito.doNothing().when(likeService).deleteLikePost(commentAndPostId);

        mockMvc.perform(delete("/api/v1/like/postId/{postId}", commentAndPostId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Post with id " + commentAndPostId + " was deleted successfully."));
    }
}