package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeViewDto;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LikeController.class)
public class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserContext userContext;
    @MockBean
    private LikeService likeService;

    private final Long postId = 1L;
    private final Long userId = 1L;
    private final Long commentId = 1L;
    private final Long likeId = 1L;
    private final LikeViewDto likeViewDto = new LikeViewDto();

    @BeforeEach
    public void setup() {
        likeViewDto.setId(likeId);
        likeViewDto.setUserId(userId);
    }

    @Test
    @DisplayName("likePost: валидные postId и userId, возвращает LikeViewDto")
    public void givenValidPostAndUserWhenLikePostThenReturnLikeViewDto() throws Exception {
        likeViewDto.setPostId(postId);

        Mockito.when(userContext.getUserId()).thenReturn(userId);
        Mockito.when(likeService.likePost(postId, userId)).thenReturn(likeViewDto);

        mockMvc.perform(post("/likes/posts/" + postId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(likeId))
                .andExpect(jsonPath("$.postId").value(postId))
                .andExpect(jsonPath("$.userId").value(userId));
        Mockito.verify(likeService).likePost(postId, userId);
    }

    @Test
    @DisplayName("unlikePost: валидные postId и userId, возвращает статус NoContent")
    public void givenValidPostAndUserWhenUnlikePostThenReturnNoContent() throws Exception {
        Mockito.when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(delete("/likes/posts/" + postId)
                        .header("x-user-id", userId))
                .andExpect(status().isNoContent());

        Mockito.verify(likeService).unlikePost(postId, userId);
    }

    @Test
    @DisplayName("likeComment: валидные commentId и userId, возвращает LikeViewDto")
    public void givenValidCommentAndUserWhenLikeCommentThenReturnLikeViewDto() throws Exception {
        likeViewDto.setCommentId(commentId);
        Mockito.when(userContext.getUserId()).thenReturn(userId);
        Mockito.when(likeService.likeComment(commentId, userId)).thenReturn(likeViewDto);

        mockMvc.perform(post("/likes/comments/" + commentId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(likeViewDto.getId()))
                .andExpect(jsonPath("$.commentId").value(commentId))
                .andExpect(jsonPath("$.userId").value(userId));

        Mockito.verify(likeService).likeComment(commentId, userId);
    }

    @Test
    @DisplayName("unlikeComment: валидные commentId и userId, возвращает статус NoContent")
    public void givenValidCommentAndUserWhenUnlikeCommentThenReturnNoContent() throws Exception {
        Mockito.when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(delete("/likes/comments/" + commentId)
                        .header("x-user-id", userId))
                .andExpect(status().isNoContent());

        Mockito.verify(likeService).unlikeComment(commentId, userId);
    }
}
