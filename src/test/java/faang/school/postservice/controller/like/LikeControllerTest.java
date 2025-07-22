package faang.school.postservice.controller.like;

import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    private MockMvc mockMvc;
    @Mock
    private LikeService likeService;
    @InjectMocks
    private LikeController likeController;

    private static final long POST_ID = 1;
    private static final long COMMENT_ID = 2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

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