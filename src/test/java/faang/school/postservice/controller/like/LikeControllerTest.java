package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static faang.school.postservice.controller.like.LikeDataController.createData;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@DisplayName("Тест для LikeController")
public class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LikeService likeService;
    @MockBean
    private UserContext userContext;

    LikeDataController data = createData();

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк посту")
    public void getPostLikers_ShouldReturnListOfUsers() throws Exception {
        when(likeService.getPostLikers(LikeDataController.postId)).thenReturn(List.of(data.getUserDto()));

        mockMvc.perform(get("/likes/posts/" + LikeDataController.postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(data.getUserDto().id()))
                .andExpect(jsonPath("$[0].username").value(data.getUserDto().username()))
                .andExpect(jsonPath("$[0].email").value(data.getUserDto().email()));
        verify(likeService).getPostLikers(LikeDataController.postId);
    }

    @Test
    @DisplayName("тест получения пустого списка пользователей при отсутвии лайкнувших пост")
    public void getPostLikers_ShouldReturnEmptyList() throws Exception {
        when(likeService.getPostLikers(LikeDataController.postId)).thenReturn(List.of());

        mockMvc.perform(get("/likes/posts/" + LikeDataController.postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(likeService).getPostLikers(LikeDataController.postId);
    }

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк коментарию")
    public void getCommentLikers_ShouldReturnListOfUsers() throws Exception {
        when(likeService.getCommentLikers(LikeDataController.commentId)).thenReturn(List.of(data.getUserDto()));

        mockMvc.perform(get("/likes/comments/" + LikeDataController.commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(data.getUserDto().id()))
                .andExpect(jsonPath("$[0].email").value(data.getUserDto().email()))
                .andExpect(jsonPath("$[0].username").value(data.getUserDto().username()));
        verify(likeService).getCommentLikers(LikeDataController.commentId);
    }

    @Test
    @DisplayName("тест получения пустого списка пользователей при отсутвии лайкнувших коментарий")
    public void getCommentLikers_ShouldReturnEmptyList() throws Exception {
        when(likeService.getCommentLikers(LikeDataController.commentId)).thenReturn(List.of());

        mockMvc.perform(get("/likes/comments/" + LikeDataController.commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(likeService).getCommentLikers(LikeDataController.commentId);
    }
}

