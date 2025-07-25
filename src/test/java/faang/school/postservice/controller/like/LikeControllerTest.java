package faang.school.postservice.controller.like;

import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = LikeController.class)
class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LikeService likeService;

    private static final long POST_ID = 1;
    private static final long COMMENT_ID = 2;

    @Test
    @DisplayName("Успешно добавить лайк посту")
    void positive_shouldAddToPost() throws Exception {
        mockMvc.perform(post("/v1/likes/posts/{id}", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(likeService, times(1)).addToPost(POST_ID);
    }

    @Test
    @DisplayName("Успешно добавить лайк комментарию")
    void positive_shouldAddToComment() throws Exception {
        mockMvc.perform(post("/v1/likes/comments/{id}", COMMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(likeService, times(1)).addToComment(COMMENT_ID);
    }

    @Test
    @DisplayName("Успешно удалить лайк у поста")
    void positive_shouldDeleteFromPost() throws Exception {
        mockMvc.perform(delete("/v1/likes/posts/{id}", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(likeService, times(1)).deleteFromPost(POST_ID);
    }

    @Test
    @DisplayName("Успешно удалить лайк у комментария")
    void positive_shouldDeleteFromComment() throws Exception {
        mockMvc.perform(delete("/v1/likes/comments/{id}", COMMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(likeService, times(1)).deleteFromComment(COMMENT_ID);
    }
}