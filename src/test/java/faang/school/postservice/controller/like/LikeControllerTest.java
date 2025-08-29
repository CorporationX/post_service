package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

    private long userId = 1L;
    private String userName = "User";
    private String userEmail = "@123";
    private long postId = 2L;
    private UserDto userDto = new UserDto(userId, userName, userEmail);
    private long commentId = 1L;

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк посту")
    public void getPostLikers_ShouldReturnListOfUsers() throws Exception {
        when(likeService.getPostLikers(postId)).thenReturn(List.of(userDto));

        mockMvc.perform(get("/likes/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto.id()))
                .andExpect(jsonPath("$[0].username").value(userDto.username()))
                .andExpect(jsonPath("$[0].email").value(userDto.email()));
        verify(likeService).getPostLikers(postId);
    }

    @Test
    @DisplayName("тест получения пустого списка пользователей при отсутвии лайкнувших пост")
    public void getPostLikers_ShouldReturnEmptyList() throws Exception {
        when(likeService.getPostLikers(postId)).thenReturn(List.of());

        mockMvc.perform(get("/likes/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(likeService).getPostLikers(postId);
    }

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк коментарию")
    public void getCommentLikers_ShouldReturnListOfUsers() throws Exception {
        when(likeService.getCommentLikers(commentId)).thenReturn(List.of(userDto));

        mockMvc.perform(get("/likes/comments/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto.id()))
                .andExpect(jsonPath("$[0].email").value(userDto.email()))
                .andExpect(jsonPath("$[0].username").value(userDto.username()));
        verify(likeService).getCommentLikers(commentId);
    }

    @Test
    @DisplayName("тест получения пустого списка пользователей при отсутвии лайкнувших коментарий")
    public void getCommentLikers_ShouldReturnEmptyList() throws Exception {
        when(likeService.getCommentLikers(commentId)).thenReturn(List.of());

        mockMvc.perform(get("/likes/comments/" + commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(likeService).getCommentLikers(commentId);
    }


}

