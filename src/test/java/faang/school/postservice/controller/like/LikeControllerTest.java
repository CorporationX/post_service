package faang.school.postservice.controller.like;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.like.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.like.LikeServiceImpl;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {
    private MockMvc mockMvc;

    @Mock
    private LikeServiceImpl likeService;

    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    public void testPutLikeToPost_whenLikeIsOk_thenLikeIsCreated() throws Exception {
        LikeDto expectedLikeDto = new LikeDto(1L, null, 1L);

        when(likeService.putLikeToPost(1L)).thenReturn(expectedLikeDto);

        mockMvc.perform(post("/api/v1/likes/post/{postId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId", is(1)));
    }

    @Test
    public void testPutLikeToComment_whenLikeIsOk_thenLikeIsCreated() throws Exception {
        LikeDto expectedLikeDto = new LikeDto(1L, 1L, null);

        when(likeService.putLikeToComment(1L)).thenReturn(expectedLikeDto);

        mockMvc.perform(post("/api/v1/likes/comment/{commentId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId", is(1)));
    }

    @Test
    public void testGetLikesByUser_whenUserHasLikes_thenLikesAreReturned() throws Exception {
        when(likeService.getLikesByUser()).thenReturn(List.of(new LikeDto(1L, 1L, 1L)));

        mockMvc.perform(get("/api/v1/likes/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].postId", is(1)));
    }
}
