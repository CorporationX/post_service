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

    long userId = 1L;
    String userName = "User";
    String userEmail = "@123";
    long postId = 2L;
    UserDto userDto = new UserDto(userId, userName, userEmail);
    long commentId = 1L;

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк посту")
    public void getListUsersWhoLikesThisPost_ShouldReturnListOfUsers() throws Exception {
        when(likeService.getPostLikers(postId)).thenReturn(List.of(userDto));

        mockMvc.perform(get("/likes/posts/{postId}/likes", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto.id()))
                .andExpect(jsonPath("$[0].username").value(userDto.username()))
                .andExpect(jsonPath("$[0].email").value(userDto.email()));
        verify(likeService).getPostLikers(postId);
    }

    @Test
    @DisplayName("тест получения пустого списка пользователей при отсутвии лайкнувших пост")
    public void getListUsersWhoLikesThisPost_ShouldReturnEmptyList() throws Exception {
        when(likeService.getPostLikers(postId)).thenReturn(List.of());

        mockMvc.perform(get("/likes/posts/{postId}/likes", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(likeService).getPostLikers(postId);
    }

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк коментарию")
    public void getListUsersWhoLikesThisComment_ShouldReturnListOfUsers() throws Exception {
        when(likeService.getListUsersWhoLikesThisComment(commentId)).thenReturn(List.of(userDto));

        mockMvc.perform(get("/likes/comments/{commentId}/likes", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto.id()))
                .andExpect(jsonPath("$[0].email").value(userDto.email()))
                .andExpect(jsonPath("$[0].username").value(userDto.username()));
        verify(likeService).getListUsersWhoLikesThisComment(commentId);
    }

    @Test
    @DisplayName("тест получения пустого списка пользователей при отсутвии лайкнувших коментарий")
    public void getListUsersWhoLikesThisComment_ShouldReturnEmptyList() throws Exception {
        when(likeService.getListUsersWhoLikesThisComment(commentId)).thenReturn(List.of());

        mockMvc.perform(get("/likes/comments/{postId}/likes", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(likeService).getListUsersWhoLikesThisComment(commentId);
    }


}

