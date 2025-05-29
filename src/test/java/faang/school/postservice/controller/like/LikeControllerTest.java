package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserContext userContext;

    @MockBean
    private LikeService likeService;

    private final long userId = 1L;
    private final long postId = 2L;
    private final long commentId = 3L;
    private final long invalidId = -1L;

    @Test
    void likeThePost_shouldCallLikeService() throws Exception {
        mockMvc.perform(put("/api/v1/like/post/" + postId + "/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(likeService, Mockito.times(1)).likeThePost(postId, userId);
    }

    @Test
    void likeThePost_shouldReturnBadRequestWhenPostIdIsLessThanOne() throws Exception {
        mockMvc.perform(put("/api/v1/like/post/" + invalidId + "/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void likeThePost_shouldReturnBadRequestWhenUserIdIsLessThanOne() throws Exception {
        mockMvc.perform(put("/api/v1/like/post/" + postId + "/user/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void likeTheComment_shouldCallLikeService() throws Exception {
        mockMvc.perform(put("/api/v1/like/comment/" + commentId + "/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(likeService, Mockito.times(1)).likeTheComment(commentId, userId);
    }

    @Test
    void likeTheComment_shouldReturnBadRequestWhenCommentIdIsLessThanOne() throws Exception {
        mockMvc.perform(put("/api/v1/like/comment/" + invalidId + "/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void likeTheComment_shouldReturnBadRequestWhenUserIdIsLessThanOne() throws Exception {
        mockMvc.perform(put("/api/v1/like/comment/" + commentId + "/user/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeLikeFromPost_shouldCallLikeService() throws Exception {
        mockMvc.perform(delete("/api/v1/like/post/" + postId + "/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(likeService, Mockito.times(1)).removeLikeFromPost(postId, userId);
    }

    @Test
    void removeLikeFromPost_shouldReturnBadRequestWhenPostIdIsLessThanOne() throws Exception {
        mockMvc.perform(delete("/api/v1/like/post/" + invalidId + "/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeLikeFromPost_shouldReturnBadRequestWhenCommentIdIsLessThanOne() throws Exception {
        mockMvc.perform(delete("/api/v1/like/post/" + postId + "/user/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeLikeFromComment_shouldCallLikeService() throws Exception {
        mockMvc.perform(delete("/api/v1/like/comment/" + commentId + "/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(likeService, Mockito.times(1)).removeLikeFromComment(commentId, userId);
    }

    @Test
    void removeLikeFromComment_shouldReturnBadRequestWhenCommentIdIsLessThanOne() throws Exception {
        mockMvc.perform(delete("/api/v1/like/comment/" + invalidId + "/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeLikeFromComment_shouldReturnBadRequestWhenUserIdIsLessThanOne() throws Exception {
        mockMvc.perform(delete("/api/v1/like/comment/" + commentId + "/user/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}