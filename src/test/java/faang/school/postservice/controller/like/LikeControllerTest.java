package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = LikeController.class)
public class LikeControllerTest {
    private static final long USER_ID = 5L;
    private static final long POST_ID = 2L;
    private static final long COMMENT_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @MockBean
    private UserContext userContext;

    @InjectMocks
    private LikeController likeController;

    private LikeDto postLikeDto;

    private LikeDto commentLikeDto;

    private LocalDateTime timestamp;

    @BeforeEach
    public void setUp() {
        timestamp = LocalDateTime.of(2020, 1, 1, 1, 1);
        postLikeDto = new LikeDto(USER_ID, POST_ID, null, timestamp);
        commentLikeDto = new LikeDto(USER_ID, null, COMMENT_ID, timestamp);
    }

    @Test
    @DisplayName("Проверка лайка поста")
    public void testSuccessLikePost() throws Exception {
        when(likeService.likePost(POST_ID)).thenReturn(postLikeDto);

        mockMvc.perform(post("/likes/posts/%d".formatted(POST_ID))
                        .header("x-user-id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.postId").value(POST_ID))
                .andExpect(jsonPath("$.commentId").doesNotExist());

        verify(likeService).likePost(POST_ID);
    }

    @Test
    @DisplayName("Проверка лайка поста с отрицательным id")
    public void testLikePostWithNegativeId() throws Exception {
        mockMvc.perform(post("/likes/posts/%d".formatted(-POST_ID))
                        .header("x-user-id", USER_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка удаления лайка поста")
    public void testSuccessRemoveLikeOnPost() throws Exception {
        when(likeService.removeLikeOnPost(POST_ID)).thenReturn(postLikeDto);

        mockMvc.perform(delete("/likes/posts/%d".formatted(POST_ID))
                        .header("x-user-id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.postId").value(POST_ID))
                .andExpect(jsonPath("$.commentId").doesNotExist());

        verify(likeService).removeLikeOnPost(POST_ID);
    }

    @Test
    @DisplayName("Проверка удаления лайка поста с отрицательным id")
    public void testRemoveLikeOnPostWithNegativeId() throws Exception {
        mockMvc.perform(delete("/likes/posts/%d".formatted(-POST_ID))
                        .header("x-user-id", USER_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка лайка коммента")
    public void testSuccessLikeComment() throws Exception {
        when(likeService.likeComment(COMMENT_ID)).thenReturn(commentLikeDto);
        mockMvc.perform(post("/likes/comments/%d".formatted(COMMENT_ID))
                        .header("x-user-id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.postId").doesNotExist())
                .andExpect(jsonPath("$.commentId").value(COMMENT_ID));
    }

    @Test
    @DisplayName("Проверка лайка коммента с отрицательным id")
    public void testLikeCommentWithNegativeId() throws Exception {
        mockMvc.perform(post("/likes/comments/%d".formatted(-COMMENT_ID))
                        .header("x-user-id", USER_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка удаления лайка коммента")
    public void testSuccessRemoveLikeOnComment() throws Exception {
        when(likeService.removeLikeOnComment(COMMENT_ID)).thenReturn(commentLikeDto);

        mockMvc.perform(delete("/likes/comments/%d".formatted(COMMENT_ID))
                        .header("x-user-id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.postId").doesNotExist())
                .andExpect(jsonPath("$.commentId").value(COMMENT_ID));

        verify(likeService).removeLikeOnComment(COMMENT_ID);
    }

    @Test
    @DisplayName("Проверка удаления лайка коммента с отрицательным id")
    public void testRemoveLikeOnCommentWithNegativeId() throws Exception {
        mockMvc.perform(delete("/likes/comments/%d".formatted(-COMMENT_ID))
                        .header("x-user-id", USER_ID))
                .andExpect(status().isBadRequest());
    }
}
